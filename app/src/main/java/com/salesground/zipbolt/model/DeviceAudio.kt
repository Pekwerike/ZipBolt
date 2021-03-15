package com.salesground.zipbolt.model

import android.net.Uri

data class DeviceAudio(
    val audioUri: Uri,
    val audioTitle: String,
    val audioDisplayName: String,
    val audioSize: Long,
    val audioDuration: Long,
    val audioMimeType: String,
    val musicArtPath: String,
    val musicArtist: String

    )