package com.salesground.zipbolt.extensions

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri

fun Uri.getMediaDuration(context: Context): String {
    var mediaDuration: String? = ""
    MediaMetadataRetriever().also { mediaMetaDataRetriver : MediaMetadataRetriever ->
        mediaMetaDataRetriver.setDataSource(context, this)
        mediaDuration = mediaMetaDataRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
    }
    return mediaDuration ?: ""
}