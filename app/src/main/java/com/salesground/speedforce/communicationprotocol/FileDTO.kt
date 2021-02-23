package com.salesground.speedforce.communicationprotocol

import java.io.File

data class FileDTO(
    val name: String,
    val childCount: Long,
    val length: Long,
    val file: File
)
