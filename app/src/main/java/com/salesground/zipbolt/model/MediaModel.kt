package com.salesground.zipbolt.model

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri

enum class MediaCategory(private val mediaType : String) {
    IMAGE("image"),
    VIDEO("video"),
    AUDIO("audio")
}

data class MediaModel(
    val mediaUri: Uri,
    val mediaDisplayName: String?,
    val mediaDateAdded: Long,
    val mediaSize: Long,
    val mediaCategory: MediaCategory,
    val mimeType: String,
    val mediaBucketName: String,
    val mediaDuration: Long = 0,
    val mediaAlbumArtPath: String = ""
)