package com.salesground.zipbolt.repository

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.os.Build
import android.provider.MediaStore

class AudioRepository(private val context: Context) {

    fun getAllAudioFilesOnDevice() {
        val collection = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q)
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY) else
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val projection: Array<String> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.MIME_TYPE,
                MediaStore.Audio.Media.DATE_ADDED,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM
            )
        } else {
            arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.MIME_TYPE,
                MediaStore.Audio.Media.DATE_ADDED,
                MediaStore.Audio.Media.SIZE
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
            val audioDisplayNameColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
            val audioMimeTypeColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE)
            val audioDateAddedColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
            val audioSizeColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)

            while(cursor.moveToNext()){
                val audioId = cursor.getLong(audioIDColumnIndex)
                val audioDisplayName = cursor.getString(audioDisplayNameColumnIndex)
                val audioMimeType = cursor.getString(audioMimeTypeColumnIndex)
                val audioDateAdded = cursor.getLong(audioDateAddedColumnIndex)
                val audioSize = cursor.getLong(audioSizeColumnIndex)

                val audioUri = ContentUris.withAppendedId(collection, audioId)
            }
        }


    }

}