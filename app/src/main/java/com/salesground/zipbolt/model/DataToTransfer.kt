package com.salesground.zipbolt.model

import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.core.net.toUri

sealed class DataToTransfer(
    var dataDisplayName: String,
    val dataUri: Uri,
    var dataSize: Long,
    var dataType: String
) {
    override fun equals(other: Any?): Boolean {
        return when (other) {
            this -> true
            !is DataToTransfer -> false
            else -> {
                (other.dataUri == this.dataUri && other.dataDisplayName == this.dataDisplayName
                        && other.dataSize == this.dataSize && other.dataType == this.dataType)
            }
        }
    }

    override fun hashCode(): Int {
        val prime = 7
        var result = 5
        result = prime * result + dataUri.hashCode()
        result = prime * result + (dataSize xor (dataSize ushr 32)).toInt()
        result = prime * result + dataDisplayName.hashCode()
        result = prime * result + dataType.hashCode()

        return result
    }

    data class DeviceAudio(
        val audioUri: Uri,
        val audioTitle: String,
        val audioDisplayName: String,
        val audioSize: Long,
        val audioDuration: Long,
        val audioMimeType: String,
        val musicArtPath: String,
        val musicArtist: String

    ) : DataToTransfer(
        dataDisplayName = audioDisplayName,
        dataUri = audioUri,
        dataSize = audioSize,
        dataType = audioMimeType
    )

    data class DeviceImage(
        val imageId: Long,
        val imageUri: Uri,
        val imageDateModified: String,
        var imageDisplayName: String = "",
        var imageMimeType: String = "",
        var imageSize: Long = 0L,
        val imageBucketName: String
    ) : DataToTransfer(
        dataDisplayName = imageDisplayName,
        dataUri = imageUri,
        dataSize = imageSize,
        dataType = imageMimeType
    )

    data class DeviceVideo(
        val videoId: Long,
        val videoUri: Uri,
        val videoDisplayName: String,
        val videoDuration: Long,
        val videoSize: Long,
        val videoBucketName: String,
        val videoMimeType: String,
        val videoDateModified: String
    ) : DataToTransfer(
        dataDisplayName = videoDisplayName,
        dataUri = videoUri,
        dataSize = videoSize,
        dataType = videoMimeType
    )

    data class DeviceApplication(
        val applicationName: String?,
        val apkPath: String,
        val appIcon: Drawable?,
        val appSize: Long
    ) : DataToTransfer(
        dataDisplayName = applicationName ?: "Unknow App",
        dataUri = apkPath.toUri(),
        dataSize = appSize,
        dataType = ".apk"
    )
}

