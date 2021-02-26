package com.salesground.zipbolt.repository

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.salesground.zipbolt.model.MediaCategory
import com.salesground.zipbolt.model.MediaModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class VideoRepository(private val context: Context) {

    fun getAllVideoFromDeviceAsFlow(): Flow<MediaModel> = flow {
        val collection: Uri = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q)
            MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY) else
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val projection: Array<String> = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DATE_ADDED,
            MediaStore.Video.Media.MIME_TYPE
        )
        val selection = null
        val selectionArguments = null
        val sortOrder = "${MediaStore.Video.Media.DATE_ADDED} ASC"

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

                val videoUri =
                    ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, videoId)

                emit(
                    MediaModel(
                        mediaUri = videoUri,
                        mediaDisplayName = videoDisplayName,
                        mediaDateAdded = videoDateAdded,
                        mediaSize = videoSize,
                        mediaCategory = MediaCategory.VIDEO,
                        mimeType = videoMimeType
                    )
                )
            }
        }

    }


}