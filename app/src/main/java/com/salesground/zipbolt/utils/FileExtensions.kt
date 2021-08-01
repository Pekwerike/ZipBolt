package com.salesground.zipbolt.utils

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import java.io.File
import java.math.MathContext
import java.util.*

fun Uri.getMediaDuration(context: Context): String {
    var mediaDuration: String? = ""
    MediaMetadataRetriever().also { mediaMetaDataRetriver: MediaMetadataRetriever ->
        try {
            mediaMetaDataRetriver.setDataSource(context, this)
            mediaDuration =
                mediaMetaDataRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            mediaMetaDataRetriver.release()
        } catch (setDataSourceFailed: RuntimeException) {
            mediaDuration = null

        }
    }
    return mediaDuration ?: "1"
}

fun Long.transformDataSizeToMeasuredUnit(percentSize: Long): String {
    return when (this) {
        in 0..999 -> "${percentSize}/${this}b"
        in 1000..999_999 -> {
            "${
                (percentSize / 1000F).toBigDecimal().round(MathContext(3))
            }/${(this / 1000F).toBigDecimal().round(MathContext(3))}kb"
        }
        in 1_000_000..9_999_999 -> {
            "${
                (percentSize / 1_000_000F).toBigDecimal().round(MathContext(2))
            }/${(this / 1_000_000F).toBigDecimal().round(MathContext(2))}mb"
        }
        in 10_000_000..999_999_999 -> {
            "${
                (percentSize / 1_000_000F).toBigDecimal().round(MathContext(3))
            }/${(this / 1_000_000F).toBigDecimal().round(MathContext(3))}mb"
        }
        in 1_000_000_000..999_999_999_999 -> {
            "${
                (percentSize / 1_000_000_000F).toBigDecimal().round(MathContext(3))
            }/${(this / 1_000_000_000F).toBigDecimal().round(MathContext(3))}gb"
        }
        else -> {
            "${
                (percentSize / 1_000_000_000_000F).toBigDecimal().round(MathContext(3))
            }/${this / 1_000_000_000_000F}tb"
        }
    }
}

fun Long.transformDataSizeToMeasuredUnit(): String {
    return when (this) {
        in 0..999 -> "${this}b"
        in 1000..999_999 -> "${(this / 1000F).toBigDecimal().round(MathContext(3))}kb"
        in 1_000_000..9_999_999 -> "${(this / 1_000_000F).toBigDecimal().round(MathContext(2))}mb"
        in 10_000_000..999_999_999 -> "${
            (this / 1_000_000F).toBigDecimal().round(MathContext(3))
        }mb"
        in 1_000_000_000..999_999_999_999 -> "${
            (this / 1_000_000_000F).toBigDecimal().round(MathContext(3))
        }gb"
        else -> "${this / 1_000_000_000_000F}tb"
    }
}

fun File.getDirectorySize(): Long {
    var directorySize = 0L
    val directoryStack: Deque<File> = ArrayDeque()
    directoryStack.push(this)
    var directory: File

    while (directoryStack.isNotEmpty()) {
        directory = directoryStack.pop()
        directory.listFiles()?.forEach {
            if (it.isDirectory) {
                directoryStack.add(it)
            } else {
                directorySize += it.length()
            }
        }
    }
    return directorySize
}