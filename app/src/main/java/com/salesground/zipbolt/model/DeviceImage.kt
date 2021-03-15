package com.salesground.zipbolt.model

import android.net.Uri

data class DeviceImage(
    val imageUri: Uri,
    val imageDateModified: String,
    val imageDisplayName: String,
    val imageMimeType: String,
    val imageBucketName: String
)
