package com.salesground.zipbolt.model

import android.content.ContentResolver
import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.DocumentsContract
import android.webkit.MimeTypeMap
import androidx.core.net.toUri
import java.io.File
import java.util.*

sealed class DataToTransfer(
    var dataDisplayName: String,
    val dataUri: Uri,
    var dataSize: Long,
    var dataType: Int,
    var percentTransferred: Float = 0f,
    var transferStatus: TransferStatus = TransferStatus.TRANSFER_WAITING,
    var documentType: DocumentType = DocumentType.Directory
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
        when {
            dataDisplayName.endsWith("jpg") || dataDisplayName.endsWith("png")
                    || dataDisplayName.endsWith("jpeg") || dataDisplayName.endsWith("gif")
                    || dataDisplayName.endsWith("webp") -> {
                documentType = DocumentType.Image
                return documentType
            }
            dataDisplayName.endsWith("mp4") || dataDisplayName.endsWith("3gp") ||
                    dataDisplayName.endsWith("webm") -> {
                documentType = DocumentType.Video
                return documentType
            }
            dataDisplayName.endsWith("pdf") -> {
                documentType = DocumentType.Document.Pdf
                return documentType
            }
            dataDisplayName.endsWith("docx") || dataDisplayName.endsWith("doc") -> {
                documentType = DocumentType.Document.WordDocument
                return documentType
            }
            dataDisplayName.endsWith("xlsx") -> {
                documentType = DocumentType.Document.ExcelDocument
                return documentType
            }
            dataDisplayName.endsWith("ppt") || dataDisplayName.endsWith("pptx") -> {
                documentType = DocumentType.Document.PowerPointDocument
                return documentType
            }
            dataDisplayName.endsWith("zip") -> {
                documentType = DocumentType.Document.ZipDocument
                return documentType
            }
        }
        // if file is not an image go further processing
        if (ContentResolver.SCHEME_CONTENT == dataUri.scheme
        ) {
            context.contentResolver.run {
                getType(dataUri)?.let {
                    documentType = getMediaType(it)
                    return documentType
                }
            }
        } else {
            val fileExtension = MimeTypeMap.getFileExtensionFromUrl(
                dataUri.toString()
            )

            MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                fileExtension.toLowerCase(Locale.ROOT)
            )?.let {
                documentType = getMediaType(it)
                return documentType
            }
        }
        documentType = DocumentType.Document.UnknownDocument
        return documentType
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DataToTransfer) return false

        if (dataDisplayName != other.dataDisplayName) return false
        if (dataUri != other.dataUri) return false
        if (dataSize != other.dataSize) return false
        if (dataType != other.dataType) return false
        if (percentTransferred != other.percentTransferred) return false
        if (transferStatus != other.transferStatus) return false
        if (documentType != other.documentType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = dataDisplayName.hashCode()
        result = 31 * result + dataUri.hashCode()
        result = 31 * result + dataSize.hashCode()
        result = 31 * result + dataType
        result = 31 * result + percentTransferred.hashCode()
        result = 31 * result + transferStatus.hashCode()
        result = 31 * result + documentType.hashCode()
        return result
    }
}

