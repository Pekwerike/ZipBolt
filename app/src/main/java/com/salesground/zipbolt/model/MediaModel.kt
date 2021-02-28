package com.salesground.zipbolt.model

import android.net.Uri

enum class MediaCategory {
    IMAGE,
    VIDEO
}

data class MediaModel(
    val mediaUri: Uri,
    val mediaDisplayName: String,
    val mediaDateAdded: Long,
    val mediaSize: Long,
    val mediaCategory: MediaCategory,
    val mimeType: String,
    val bucketName: String
)