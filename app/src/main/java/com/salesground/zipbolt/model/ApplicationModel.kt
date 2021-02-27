package com.salesground.zipbolt.model

import android.graphics.drawable.Drawable

data class ApplicationModel(
    val applicationName: String?,
    val apkPath: String,
    val appIcon: Drawable?,
    val appSize: Long
)