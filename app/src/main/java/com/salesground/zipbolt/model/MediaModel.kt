package com.salesground.zipbolt.model

import android.net.Uri

abstract class MediaModel(
    val uri: Uri,
    val name: String,
    val dateAdded: Long,
    val size: Long
)