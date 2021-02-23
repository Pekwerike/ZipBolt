package com.salesground.speedforce.communicationprotocol

import java.io.File

/**
 * Parameters
 * 1. name -> Single file or folder name
 * 2. childCount -> folders have a childCount > 0, while single files have a count equal to 0
 * 3. length -> folders have a file length of 0, while single files have a length > 0
 * 4. file -> raw file to be transferred, for folders, file = null, while for single files, file has an actual value
 */
data class FileDTO(
    val name: String,
    val childCount: Long,
    val length: Long,
    val file: File?
)
