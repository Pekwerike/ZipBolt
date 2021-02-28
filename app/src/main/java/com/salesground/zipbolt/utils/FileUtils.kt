package com.salesground.zipbolt.utils

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri

fun getMediaDuration(context: Context, mediaUri: Uri): String {
    var mediaDuration: String? = ""
    MediaMetadataRetriever().apply {
        this.setDataSource(context, mediaUri)
        mediaDuration = this.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
    }
    return mediaDuration ?: ""
}