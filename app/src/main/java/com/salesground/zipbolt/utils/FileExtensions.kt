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