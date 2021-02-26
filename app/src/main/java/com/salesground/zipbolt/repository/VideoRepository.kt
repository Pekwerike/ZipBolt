package com.salesground.zipbolt.repository

import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.salesground.zipbolt.model.MediaModel

class VideoRepository (private val context : Context) {

    suspend fun getAllVideoFromDevice() : MutableList<MediaModel>{
        val collection : Uri = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        }else {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        }
    }
}