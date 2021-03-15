package com.salesground.zipbolt.repository

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.os.Build
import android.provider.MediaStore
import com.salesground.zipbolt.extensions.getMediaDuration
import com.salesground.zipbolt.model.MediaCategory
import com.salesground.zipbolt.model.MediaModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AudioRepository @Inject constructor(@ApplicationContext private val context: Context) {

    

    fun getAllAudioFilesOnDeviceList(): MutableList<MediaModel> {
        val allAudioFilesOnDevice: MutableList<MediaModel> = mutableListOf()

        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY) else
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val projection: Array<String> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.MIME_TYPE,
                MediaStore.Audio.Media.DATE_ADDED,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM_ID
            )
        } else {
            arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.MIME_TYPE,
                MediaStore.Audio.Media.DATE_ADDED,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.ALBUM_ID
            )
        }

        val selection = null
        val selectionArgs = null
        val sortOrder = "${MediaStore.Audio.Media.DATE_ADDED} DESC"

        context.contentResolver.query(
            collection,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.let { cursor: Cursor ->
            val audioIDColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val audioDisplayNameColumnIndex =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
            val audioMimeTypeColumnIndex =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE)
            val audioDateAddedColumnIndex =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
            val audioSizeColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
            val audioAlbumIdColumnIndex =
                cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)
            val audioTitleColumnIndex = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)



            while (cursor.moveToNext()) {
                val audioId = cursor.getLong(audioIDColumnIndex)
                val audioDisplayName = cursor.getString(audioDisplayNameColumnIndex)
                val audioMimeType = cursor.getString(audioMimeTypeColumnIndex)
                val audioDateAdded = cursor.getLong(audioDateAddedColumnIndex)
                val audioSize = cursor.getLong(audioSizeColumnIndex)
                val audioUri = ContentUris.withAppendedId(collection, audioId)
                val audioDuration = audioUri.getMediaDuration(context)
                val audioAlbumId = cursor.getLong(audioAlbumIdColumnIndex)
                val audioTitle = cursor.getString(audioTitleColumnIndex)

                var audioAlbumArtPath = ""
                var audioAlbumArtist = ""
                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                    context.contentResolver.query(
                        MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                        arrayOf(
                            MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM_ART,
                            MediaStore.Audio.Albums.ARTIST
                        ),
                        "${MediaStore.Audio.Albums._ID} =?",
                        arrayOf(audioAlbumId.toString()),
                        "${MediaStore.Audio.Albums._ID} DESC LIMIT 1"
                    )?.let {
                        if (it.moveToFirst()) {
                            audioAlbumArtPath =
                                it.getString(it.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART))
                                    ?: ""
                            audioAlbumArtist =
                                it.getString(it.getColumnIndex(MediaStore.Audio.Albums.ARTIST))
                                    ?: ""
                        }
                    }
                }

                allAudioFilesOnDevice.add(
                    MediaModel(
                        mediaUri = audioUri,
                        mediaDisplayName = audioDisplayName,
                        mediaDateAdded = audioDateAdded,
                        mediaSize = audioSize,
                        mediaCategory = MediaCategory.AUDIO,
                        mediaBucketName = "",
                        mediaDuration = audioDuration.toLong(),
                        mimeType = audioMimeType,
                        mediaAlbumArtPath = audioAlbumArtPath,
                        mediaTitle = audioTitle,
                        mediaArtist = audioAlbumArtist
                    )
                )
            }
            cursor.close()
        }
        return allAudioFilesOnDevice
    }
}