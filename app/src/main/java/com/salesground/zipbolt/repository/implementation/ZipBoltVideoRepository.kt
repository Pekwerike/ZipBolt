package com.salesground.zipbolt.repository.implementation

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.salesground.zipbolt.communication.MediaTransferProtocol
import com.salesground.zipbolt.communication.readStreamDataIntoFile
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.repository.SavedFilesRepository
import com.salesground.zipbolt.repository.VideoRepositoryI
import com.salesground.zipbolt.repository.ZIP_BOLT_MAIN_DIRECTORY
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.DataInputStream
import java.io.File
import javax.inject.Inject

class ZipBoltVideoRepository @Inject constructor(
    savedFilesRepository: SavedFilesRepository,
    @ApplicationContext val context: Context
) : VideoRepositoryI {
    private val zipBoltVideosFolder: File =
        savedFilesRepository.getZipBoltMediaCategoryBaseDirectory(SavedFilesRepository.ZipBoltMediaCategory.VIDEOS_BASE_DIRECTORY)

    private val contentValues = ContentValues()

    private fun checkIfVideoWithNameExistsInMediaStore(
        videoName: String,
    ): Boolean {
        val collection: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY) else
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI

        val projection: Array<String> = arrayOf(MediaStore.Video.Media.DISPLAY_NAME)
        val selection = "${MediaStore.Video.Media.DISPLAY_NAME} = ?"
        val selectionArgs = arrayOf(videoName)
        val sortOrder = "${MediaStore.Video.Media.DISPLAY_NAME} ASC LIMIT 1"

        context.contentResolver.query(
            collection,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )
            ?.let { cursor ->
                return cursor.moveToFirst()
            }
        return false
    }

    override suspend fun insertVideoIntoMediaStore(
        videoName: String,
        videoSize: Long,
        dataInputStream: DataInputStream,
        transferMetaDataUpdateListener: MediaTransferProtocol.TransferMetaDataUpdateListener,
        dataReceiveListener: MediaTransferProtocol.DataReceiveListener
    ) {
        val videoFile: File = if (checkIfVideoWithNameExistsInMediaStore(videoName)) {
            File(zipBoltVideosFolder, "Vid_${Math.random()}$videoName")
        } else {
            File(zipBoltVideosFolder, videoName)
        }

        val currentTime = System.currentTimeMillis()

        contentValues.clear()
        contentValues.run {
            put(MediaStore.Video.Media.DISPLAY_NAME, videoFile.name)
            put(MediaStore.Video.Media.TITLE, videoFile.name)
            put(MediaStore.Video.Media.SIZE, videoSize)
            put(MediaStore.Video.Media.MIME_TYPE, "video/*")
            put(MediaStore.Video.Media.DATE_MODIFIED, currentTime / 1000)
            put(MediaStore.Video.Media.DATE_ADDED, currentTime / 1000)
            put(MediaStore.Video.Media.DATA, videoFile.absolutePath)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Video.Media.OWNER_PACKAGE_NAME, context.packageName)
                put(MediaStore.Video.Media.DATE_TAKEN, currentTime)
                put(MediaStore.Video.Media.IS_PENDING, 1)
                put(
                    MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
                    ZIP_BOLT_MAIN_DIRECTORY
                )
            }
        }

        // percentage of bytes read is 0%
        dataReceiveListener.onReceive(
            videoName,
            videoSize,
            0f,
            DataToTransfer.MediaType.VIDEO.value,
            null,
            DataToTransfer.TransferStatus.RECEIVE_STARTED
        )

        if (!dataInputStream.readStreamDataIntoFile(
                dataReceiveListener = dataReceiveListener,
                dataDisplayName = videoName,
                size = videoSize,
                transferMetaDataUpdateListener = transferMetaDataUpdateListener,
                receivingFile = videoFile,
                dataType = DataToTransfer.MediaType.VIDEO
            )
        ) {
            // video receive was cancelled
            return
        }

        context.contentResolver.insert(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )?.let { videoUri ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.Video.Media.IS_PENDING, 0)
                context.contentResolver.update(videoUri, contentValues, null, null)
            }

            // percentage of image read is 100% with the image uri
            dataReceiveListener.onReceive(
                videoName,
                videoSize,
                100f,
                DataToTransfer.MediaType.VIDEO.value,
                videoUri,
                DataToTransfer.TransferStatus.RECEIVE_COMPLETE
            )
        }
    }


    override suspend fun getVideosOnDevice(): MutableList<DataToTransfer> {
        val videosOnDevice = mutableListOf<DataToTransfer>()
        val collection: Uri = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q)
            MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY) else
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI

        val projection: Array<String> = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.MIME_TYPE
        )

        val sortOrder = "${MediaStore.Video.Media.DATE_MODIFIED} DESC"

        context.contentResolver.query(
            collection,
            projection,
            null, null,
            sortOrder
        )?.let { cursor ->
            val videoIdColumnIndex = cursor.getColumnIndex(MediaStore.Video.Media._ID)
            val videoDisplayNameColumnIndex =
                cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME)
            val videoSizeColumnIndex = cursor.getColumnIndex(MediaStore.Video.Media.SIZE)
            val videoDurationColumnIndex = cursor.getColumnIndex(MediaStore.Video.Media.DURATION)
            val videoMimeTypeColumnIndex = cursor.getColumnIndex(MediaStore.Video.Media.MIME_TYPE)

            while (cursor.moveToNext()) {
                val videoId = cursor.getLong(videoIdColumnIndex)
                videosOnDevice.add(
                    DataToTransfer.DeviceVideo(
                        videoId = cursor.getLong(videoIdColumnIndex),
                        videoDisplayName = cursor.getString(videoDisplayNameColumnIndex) ?: "Name not specified",
                        videoSize = cursor.getLong(videoSizeColumnIndex),
                        videoDuration = cursor.getLong(videoDurationColumnIndex),
                        videoUri = ContentUris.withAppendedId(collection, videoId),
                        videoMimeType = cursor.getString(videoMimeTypeColumnIndex) ?: "Video"
                    )
                )

            }
        }
        return videosOnDevice
    }
}
