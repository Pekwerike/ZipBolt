package com.salesground.zipbolt.utils

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri

fun Uri.getMediaDuration(context: Context): String {
    var mediaDuration: String? = ""
    MediaMetadataRetriever().also { mediaMetaDataRetriver: MediaMetadataRetriever ->
        try {
            mediaMetaDataRetriver.setDataSource(context, this)
            mediaDuration =
                mediaMetaDataRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            mediaMetaDataRetriver.release()
        } catch (setDataSourceFailed: RuntimeException) {
            mediaDuration = null

        }
    }
    return mediaDuration ?: "1"
}

fun Long.transformDataSizeToMeasuredUnit(): String {
    return when {
        this in 1..99 -> "${this}b"
        this in 1000..999_999 -> "${this / 1000}kb"
        this in 1_000_000..999_999_999 -> "${this / 1_000_000}mb"
        this in 1_000_000_000..999_999_999_999 -> "${this / 1_000_000_000}gb"
        else -> "${this / 1_000_000_000_000}tb"
    }
}