package com.salesground.zipbolt.repository.implementation

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.salesground.zipbolt.communication.MediaTransferProtocol
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.repository.SavedFilesRepository
import com.salesground.zipbolt.repository.VideoRepositoryI
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.DataInputStream
import javax.inject.Inject

class ZipBoltVideosRepository @Inject constructor(
    private val savedFilesRepository: SavedFilesRepository,
) : VideoRepositoryI {
    override suspend fun insertVideoIntoMediaStore(
        videoName: String,
        videoSize: Long,
        dataInputStream: DataInputStream,
        transferMetaDataUpdateListener: MediaTransferProtocol.TransferMetaDataUpdateListener,
        dataReceiveListener: MediaTransferProtocol.DataReceiveListener
    ) {

    }

    override suspend fun getMetaDataOfVideo(video: DataToTransfer.DeviceVideo): DataToTransfer {

    }

    override suspend fun getVideosOnDevice(context: Context): MutableList<DataToTransfer> {

        val collection: Uri = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q)
            MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY) else
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI

        val projection: Array<String> = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DURATION
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

            while (cursor.moveToNext()) {
                val videoId = cursor.getLong(videoIdColumnIndex)
                val videoName = cursor.getString(videoDisplayNameColumnIndex)
                val videoSize = cursor.getLong(videoSizeColumnIndex)
                val videoDuration = cursor.getLong(videoDurationColumnIndex)

                val videoUri =  ContentUris.withAppendedId(collection, videoId)
            }
        }

    }