package com.salesground.zipbolt.repository

import android.content.Context
import android.os.Build
import android.provider.MediaStore

class AudioRepository(private val context: Context) {

    fun getAllAudioFilesOnDevice() {
        val collection = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q)
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY) else
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

    }

}