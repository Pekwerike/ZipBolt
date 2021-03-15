package com.salesground.zipbolt.repository

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.salesground.zipbolt.extensions.getMediaDuration
import com.salesground.zipbolt.model.MediaCategory
import com.salesground.zipbolt.model.MediaModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class VideoRepository @Inject constructor(@ApplicationContext private val context: Context) {

    fun getAllVideoFromDevicePreviewList(): MutableList<MediaModel> {
        val videosOnDevicePreviewList: MutableList<MediaModel> = mutableListOf()
        val collection: Uri = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q)
            MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY) else
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val projection: Array<String> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            arrayOf(
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.DATE_ADDED,
                MediaStore.Video.Media.MIME_TYPE,
                MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Video.Media.DURATION
            )
        } else {
            arrayOf(
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.DATE_ADDED,
                MediaStore.Video.Media.MIME_TYPE,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DURATION
            )
        }
        val selection = null
        val selectionArguments = null
        val sortOrder = "${MediaStore.Video.Media.DATE_ADDED} DESC LIMIT 8"

        context.contentResolver.query(
            collection,
            projection,
            selection,
            selectionArguments,
            sortOrder
        )?.let { cursor: Cursor ->
            val videoIdColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val videoDisplayNameColumnIndex =
                cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            val videoSizeColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
            val videoDateAddedColumnIndex =
                cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)
            val videoMimeTypeColumnIndex =
                cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE)

            while (cursor.moveToNext()) {
                val videoId = cursor.getLong(videoIdColumnIndex)
                val videoDisplayName = cursor.getString(videoDisplayNameColumnIndex)
                val videoSize = cursor.getLong(videoSizeColumnIndex)
                val videoDateAdded = cursor.getLong(videoDateAddedColumnIndex)
                val videoMimeType = cursor.getString(videoMimeTypeColumnIndex)

                val videoParentFolderName: String =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        val videoBucketDisplayNameColumnIndex =
                            cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)
                        cursor.getString(videoBucketDisplayNameColumnIndex)
                    } else {
                        val videoFilePath =
                            cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
                        File(cursor.getString(videoFilePath)).parentFile!!.name
                    }

                val videoUri =
                    ContentUris.withAppendedId(collection, videoId)
                val videoDuration = videoUri.getMediaDuration(context)


                videosOnDevicePreviewList.add(
                    MediaModel(
                        mediaUri = videoUri,
                        mediaDisplayName = videoDisplayName,
                        mediaDateAdded = videoDateAdded,
                        mediaSize = videoSize,
                        mediaCategory = MediaCategory.VIDEO,
                        mimeType = videoMimeType,
                        mediaBucketName = videoParentFolderName,
                        mediaDuration = videoDuration.toLong()
                    )
                )
            }
            cursor.close()
        }
        return videosOnDevicePreviewList
    }

    fun getAllVideoFromDevice(): MutableList<MediaModel> {
        val videosOnDevice: MutableList<MediaModel> = mutableListOf()
        val collection: Uri = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q)
            MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY) else
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val projection: Array<String> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            arrayOf(
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.DATE_ADDED,
                MediaStore.Video.Media.MIME_TYPE,
                MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Video.Media.DURATION
            )
        } else {
            arrayOf(
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.DATE_ADDED,
                MediaStore.Video.Media.MIME_TYPE,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DURATION
            )
        }
        val selection = null
        val selectionArguments = null
        val sortOrder = "${MediaStore.Video.Media.DATE_ADDED} DESC LIMIT 10"

        context.contentResolver.query(
            collection,
            projection,
            selection,
            selectionArguments,
            sortOrder
        )?.let { cursor: Cursor ->
            val videoIdColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val videoDisplayNameColumnIndex =
                cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            val videoSizeColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
            val videoDateAddedColumnIndex =
                cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)
            val videoMimeTypeColumnIndex =
                cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE)

            while (cursor.moveToNext()) {
                val videoId = cursor.getLong(videoIdColumnIndex)
                val videoDisplayName = cursor.getString(videoDisplayNameColumnIndex)
                val videoSize = cursor.getLong(videoSizeColumnIndex)
                val videoDateAdded = cursor.getLong(videoDateAddedColumnIndex)
                val videoMimeType = cursor.getString(videoMimeTypeColumnIndex)

                val videoParentFolderName: String =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        val videoBucketDisplayNameColumnIndex =
                            cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)
                        cursor.getString(videoBucketDisplayNameColumnIndex)
                    } else {
                        val videoFilePath =
                            cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
                        File(cursor.getString(videoFilePath)).parentFile!!.name
                    }

                val videoUri =
                    ContentUris.withAppendedId(collection, videoId)
                val videoDuration = videoUri.getMediaDuration(context)


                videosOnDevice.add(
                    MediaModel(
                        mediaUri = videoUri,
                        mediaDisplayName = videoDisplayName,
                        mediaDateAdded = videoDateAdded,
                        mediaSize = videoSize,
                        mediaCategory = MediaCategory.VIDEO,
                        mimeType = videoMimeType,
                        mediaBucketName = videoParentFolderName,
                        mediaDuration = videoDuration.toLong()
                    )
                )
            }
            cursor.close()
        }
        return videosOnDevice
    }
}