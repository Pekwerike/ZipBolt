package com.salesground.zipbolt.repository

import com.salesground.zipbolt.model.DataToTransfer
import java.io.File

interface FileRepository {
    suspend fun getRootDirectory(): File
    suspend fun getDirectoryChildren(directoryPath: String): List<DataToTransfer>

    // TODO Build out the API for this two functions
    suspend fun insertDirectory()
    suspend fun insertFile()
}