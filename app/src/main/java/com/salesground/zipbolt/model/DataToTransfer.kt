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
    var percentTransferred: Float = 0f,
    var transferStatus: TransferStatus = TransferStatus.TRANSFER_WAITING
) {
    abstract val dataType: Int

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
    ) {
        override val dataType: Int = MediaType.Audio.value
    }

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
        dataSize = imageSize
    ) {
        override val dataType: Int = MediaType.Image.value
    }

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
        dataSize = videoSize
    ) {
        override val dataType: Int = MediaType.Video.value
    }

    data class DeviceApplication(
        val applicationName: String?,
        val apkPath: String,
        val appSize: Long,
        val applicationIcon: Drawable?
    ) : DataToTransfer(
        dataDisplayName = applicationName ?: "Unknown App",
        dataUri = apkPath.toUri(),
        dataSize = appSize
    ) {
        override val dataType: Int
            get() = MediaType.App.value
    }


    data class DeviceFile(
        val file: File,
    ) : DataToTransfer(
        dataDisplayName = file.name,
        dataUri = Uri.fromFile(file),
        dataSize = file.length()
    ) {
        override val dataType: Int = when {
            dataDisplayName.endsWith("jpg") || dataDisplayName.endsWith("png")
                    || dataDisplayName.endsWith("jpeg") || dataDisplayName.endsWith("gif")
                    || dataDisplayName.endsWith("webp") -> {
                MediaType.File.ImageFile.value
            }
            dataDisplayName.endsWith("mp4") || dataDisplayName.endsWith("3gp") ||
                    dataDisplayName.endsWith("webm") -> {
                MediaType.File.VideoFile.value
            }
            dataDisplayName.endsWith("mp3") -> {
                MediaType.File.AudioFile.value
            }
            dataDisplayName.endsWith("apk") -> {
                MediaType.File.AppFile.value
            }
            dataDisplayName.endsWith("pdf") -> {
                MediaType.File.Document.PdfDocument.value
            }
            dataDisplayName.endsWith("docx") || dataDisplayName.endsWith("doc") -> {
                MediaType.File.Document.WordDocument.value
            }
            dataDisplayName.endsWith("xlsx") || dataDisplayName.endsWith("xls") -> {
                MediaType.File.Document.ExcelDocument.value
            }
            dataDisplayName.endsWith("ppt") || dataDisplayName.endsWith("pptx") -> {
                MediaType.File.Document.PowerPointDocument.value
            }
            dataDisplayName.endsWith("zip") -> {
                MediaType.File.Document.ZipDocument.value
            }
            else -> MediaType.File.Document.UnknownDocument.value
        }

        fun getFileType(context: Context): Int {
            // if file is not an image go further processing
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
            return MediaType.File.Document.UnknownDocument.value
        }

        private fun getMediaType(mimeType: String): Int {
            return when {
                mimeType.contains("image", ignoreCase = true) -> {
                    MediaType.File.ImageFile.value
                }
                mimeType.contains("video", ignoreCase = true) -> {
                    MediaType.File.VideoFile.value
                }
                mimeType.contains("Pdf", ignoreCase = true) -> {
                    MediaType.File.Document.PdfDocument.value
                }
                mimeType.contains("app", ignoreCase = true) -> {
                    MediaType.File.AppFile.value
                }
                mimeType.contains("audio", ignoreCase = true) -> {
                    MediaType.File.AudioFile.value
                }
                mimeType.contains("word", ignoreCase = true) -> {
                    MediaType.File.Document.WordDocument.value
                }
                mimeType.contains(DocumentsContract.Document.MIME_TYPE_DIR, ignoreCase = true) -> {
                    MediaType.File.Directory.value
                }
                else -> {
                    MediaType.File.Document.UnknownDocument.value
                }
            }
        }
    }
}

