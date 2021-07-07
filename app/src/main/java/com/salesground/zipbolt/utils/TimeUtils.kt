package com.salesground.zipbolt.utils

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

val dateFormat = SimpleDateFormat("d MMMM, yyyy", Locale.UK)

fun Long.parseDate(): String {
    return dateFormat.format(this)
}

private fun String.splitDate(): List<String> {
    return split(" ", ignoreCase = true)
}

fun String.customizeDate(): String {
    var day: String = this
    val splitDate = splitDate()
    val presentDate = System.currentTimeMillis().parseDate().splitDate()
    // check if the month and year is the same
    if (splitDate[1] == presentDate[1] && splitDate[2] == presentDate[2]) {
        when (presentDate[0].toInt() - splitDate[0].toInt()) {
            0 -> day = "Today"
            1 -> day = "Yesterday"
            2 -> day = "Two days ago"
        }
    }
    return day
}

fun Long.formatVideoDurationToString(): String {
    val durationInSeconds = this / 1000
    val durationInMinutes = durationInSeconds / 60
    val remainingSeconds = durationInSeconds % 60

    return if (remainingSeconds > 0 && durationInMinutes > 0) {
        if (remainingSeconds < 30) {
            "${durationInMinutes}min"
        } else {
            "${durationInMinutes + 1}min"
        }
    } else if (durationInMinutes == 0L) {
        "${durationInSeconds}sec"
    } else {
        "${durationInMinutes}min"
    }
}

fun Uri.getVideoDuration(context: Context): Long {
    var videoDuration = 0L
    context.contentResolver.query(
        this,
        arrayOf(MediaStore.Video.Media.DURATION),
        null,
        null,
        null
    )?.let { cursor: Cursor ->
        cursor.moveToFirst()
        videoDuration = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION))
    }

    return videoDuration
}

fun Uri.getAudioDuration(context: Context): Long {
    var audioDuration = 0L
    context.contentResolver.query(
        this,
        arrayOf(MediaStore.Audio.Media.DURATION),
        null, null, null
    )?.let { cursor: Cursor ->
        cursor.moveToFirst()
        audioDuration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
    }
    return audioDuration
}