package com.salesground.zipbolt.repository

import com.salesground.zipbolt.communication.MediaTransferProtocol
import com.salesground.zipbolt.model.DataToTransfer
import java.io.DataInputStream
import java.io.File

interface FileRepository {
    suspend fun getRootDirectory(): File
    suspend fun getDirectoryChildren(directoryPath: String): List<DataToTransfer>

    // TODO Build out the API for this two functions
    suspend fun insertDirectory(
        originalTransferDirectoryName: String,
        baseDirectory: File,
        dataInputStream: DataInputStream,
        transferMetaDataUpdateListener: MediaTransferProtocol.TransferMetaDataUpdateListener,
        directorySize: Long,
        dataReceiveListener: MediaTransferProtocol.DataReceiveListener
    )
    suspend fun insertFile()
}