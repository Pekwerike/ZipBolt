package com.salesground.zipbolt.repository

import java.io.File

interface FileRepository {
    suspend fun getRootDirectory(): File
    suspend fun getDirectoryChildren(directoryPath: String): Array<File>

    // TODO Build out the API for this two functions
    suspend fun insertDirectory()
    suspend fun insertFile()
}