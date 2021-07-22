package com.salesground.zipbolt.model

import android.content.ContentResolver
import android.content.Context
import android.content.pm.ApplicationInfo
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.DocumentsContract
import android.webkit.MimeTypeMap
import androidx.compose.ui.text.toLowerCase
import androidx.core.net.toUri
import java.io.File
import java.util.*

sealed class DataToTransfer(
    var dataDisplayName: String,
    val dataUri: Uri,
    var dataSize: Long,
    var dataType: Int,
    var percentTransferred: Float = 0f,
    var transferStatus: TransferStatus = TransferStatus.TRANSFER_WAITING
) {
    enum class TransferStatus(val value: Int) {
        NO_ACTION(9),
        TRANSFER_STARTED(16),
        TRANSFER_WAITING(10),
        TRANSFER_COMPLETE(11),
        TRANSFER_ONGOING(13),
        RECEIVE_COMPLETE(14),
        RECEIVE_STARTED(17),
        RECEIVE_ONGOING(15),
        TRANSFER_CANCELLED(19)
    }

    enum class MediaType(val value: Int) {
        IMAGE(209),
        VIDEO(210),
        AUDIO(211),
        APP(212),
        FILE(213)
    }

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
        result = prime * result + dataUri.toString().hashCode()
        result = prime * result + (dataSize xor (dataSize ushr 32)).toInt()
        result = prime * result + dataDisplayName.hashCode()
        result = prime * result + dataType.hashCode()
        return result
    }

    data class DeviceAudio(
        val audioUri: Uri,
        val audioDisplayName: String,
        val audioSize: Long,
        val audioDuration: Long,
        val audioArtPath: Uri
    ) : DataToTransfer(
        dataDisplayName = audioDisplayName,
        dataUri = audioUri,
        dataSize = audioSize,
        dataType = MediaType.AUDIO.value
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
        dataType = MediaType.IMAGE.value
    )

    data class DeviceVideo(
        val videoId: Long,
        val videoUri: Uri,
        val videoDisplayName: String,
        val videoDuration: Long,
        val videoSize: Long,
        val videoMimeType: String = "",
    ) : DataToTransfer(
        dataDisplayName = videoDisplayName,
        dataUri = videoUri,
        dataSize = videoSize,
        dataType = MediaType.VIDEO.value
    )

    data class DeviceApplication(
        val applicationName: String?,
        val apkPath: String,
        val appSize: Long,
        val applicationIcon: Drawable?
    ) : DataToTransfer(
        dataDisplayName = applicationName ?: "Unknown App",
        dataUri = apkPath.toUri(),
        dataSize = appSize,
        dataType = MediaType.APP.value
    )

    data class DeviceFile(
        val file: File,
    ) : DataToTransfer(
        dataDisplayName = file.name,
        dataUri = Uri.fromFile(file),
        dataSize = file.length(),
        dataType = MediaType.FILE.value
    )

    fun getFileType(context: Context): DocumentType {
        if (ContentResolver.SCHEME_CONTENT == dataUri.scheme
        ) {
            context.contentResolver.run {
                getType(dataUri)?.let {
                    return getMediaType(it)
                }
            }
        } else {
            val fileExtension = MimeTypeMap.getFileExtensionFromUrl(
                dataUri.toString()
            )
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                fileExtension.toLowerCase(Locale.ROOT)
            )?.let {
                return getMediaType(it)
            }
        }
        return DocumentType.Document.UnknownDocument
    }

    private fun getMediaType(mimeType: String): DocumentType {
        return when {
            mimeType.contains("image", ignoreCase = true) -> {
                DocumentType.Image
            }
            mimeType.contains("video", ignoreCase = true) -> {
                DocumentType.Video
            }
            mimeType.contains("Pdf", ignoreCase = true) -> {
                DocumentType.Document.Pdf
            }
            mimeType.contains("app", ignoreCase = true) -> {
                DocumentType.App
            }
            mimeType.contains("audio", ignoreCase = true) -> {
                DocumentType.Audio
            }
            mimeType.contains("word", ignoreCase = true) -> {
                DocumentType.Document.WordDocument
            }
            mimeType.contains(DocumentsContract.Document.MIME_TYPE_DIR, ignoreCase = true) -> {
                DocumentType.Directory
            }
            else -> {
                DocumentType.Document.UnknownDocument
            }
        }
    }
}

