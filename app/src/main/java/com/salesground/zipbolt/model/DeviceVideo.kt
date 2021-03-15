package com.salesground.zipbolt.model

import android.net.Uri

data class DeviceVideo(
    val videoId: Long,
    val videoUri: Uri,
    val videoDuration: Long,
    val videoSize: Long,
    val videoBucketName: String,
    val videoMimeType : String,
    val videoDateModified: String
)

