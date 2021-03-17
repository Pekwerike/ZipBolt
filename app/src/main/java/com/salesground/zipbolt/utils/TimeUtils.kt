package com.salesground.zipbolt.utils

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

val dateFormat = SimpleDateFormat("MMMM, yyyy", Locale.UK)

fun Long.parseDate(): String {
    return dateFormat.format(this)
}