package com.salesground.zipbolt.model

import android.graphics.drawable.Drawable
import android.net.Uri

sealed class DataToTransfer {
    data class DeviceAudio(
        val audioUri: Uri,
        val audioTitle: String,
        val audioDisplayName: String,
        val audioSize: Long,
        val audioDuration: Long,
        val audioMimeType: String,
        val musicArtPath: String,
        val musicArtist: String

    ) : DataToTransfer()

    data class DeviceImage(
        val imageUri: Uri,
        val imageDateModified: String,
        val imageDisplayName: String = "",
        val imageMimeType: String = "",
        val imageSize: Long = 0L,
        val imageBucketName: String
    ) : DataToTransfer()

    data class DeviceVideo(
        val videoId: Long,
        val videoUri: Uri,
        val videoDuration: Long,
        val videoSize: Long,
        val videoBucketName: String,
        val videoMimeType: String,
        val videoDateModified: String
    ) : DataToTransfer()

    data class DeviceApplication(
        val applicationName: String?,
        val apkPath: String,
        val appIcon: Drawable?,
        val appSize: Long
    ) : DataToTransfer()
}

