package com.salesground.zipbolt.repository

import com.salesground.zipbolt.communication.MediaTransferProtocol
import com.salesground.zipbolt.model.DataToTransfer
import java.io.DataInputStream
import java.io.File

interface FileRepository {
    suspend fun getRootDirectory(): File
    suspend fun getDirectoryChildren(directoryPath: String): List<DataToTransfer>
}