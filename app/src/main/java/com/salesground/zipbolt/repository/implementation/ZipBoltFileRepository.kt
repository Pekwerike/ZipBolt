package com.salesground.zipbolt.repository.implementation

import android.os.Environment
import com.salesground.zipbolt.communication.MediaTransferProtocol
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.model.MediaType
import com.salesground.zipbolt.repository.FileRepository
import com.salesground.zipbolt.service.DataTransferService
import java.io.BufferedOutputStream
import java.io.DataInputStream
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import kotlin.math.min

class ZipBoltFileRepository  @Inject constructor(
) : FileRepository {
    private val dataBuffer = ByteArray(DataTransferService.BUFFER_SIZE)
    private var sizeOfDataReadFromDirectoryTransfer: Long = 0L
    override suspend fun getRootDirectory(): File {
        return Environment.getExternalStorageDirectory()
    }

    override suspend fun insertDirectory(
        originalTransferDirectoryName: String,
        baseDirectory: File,
        dataInputStream: DataInputStream,
        transferMetaDataUpdateListener: MediaTransferProtocol.TransferMetaDataUpdateListener,
        directorySize: Long,
        dataReceiveListener: MediaTransferProtocol.DataReceiveListener
    ) {
        // read directory name
        val directoryName = dataInputStream.readUTF()
        // create directory file
        val directoryFile = File(baseDirectory, directoryName)
        val directoryChildrenCount = dataInputStream.readInt()

        for (i in 0 until directoryChildrenCount) {
            // read child type
            val childType = dataInputStream.readInt()
            if (childType == MediaType.Directory.value) {
                // nested directory
                insertDirectory(
                    originalTransferDirectoryName,
                            directoryFile,
                    dataInputStream,
                    transferMetaDataUpdateListener,
                    directorySize,
                    dataReceiveListener
                )
            } else {
                // read file name
                val fileName = dataInputStream.readUTF()
                // read file size
                var fileSize = dataInputStream.readLong()
                val directoryChildFile = File(directoryFile, fileName)
                val childFileOS = BufferedOutputStream(FileOutputStream(directoryChildFile))
                while (fileSize > 0) {
                    when (dataInputStream.readInt()) {
                        MediaTransferProtocol.MediaTransferProtocolMetaData.KEEP_RECEIVING.value -> {

                        }
                        MediaTransferProtocol.MediaTransferProtocolMetaData.CANCEL_ACTIVE_RECEIVE.value -> {
                            // delete image file
                            directoryChildFile.delete()
                            return
                        }
                        MediaTransferProtocol.MediaTransferProtocolMetaData.KEEP_RECEIVING_BUT_CANCEL_ACTIVE_TRANSFER.value -> {
                            transferMetaDataUpdateListener.onMetaTransferDataUpdate(
                                MediaTransferProtocol.MediaTransferProtocolMetaData.KEEP_RECEIVING_BUT_CANCEL_ACTIVE_TRANSFER
                            )
                        }
                    }

                    dataInputStream.readFully(
                        dataBuffer, 0,
                        min(dataBuffer.size.toLong(), fileSize).toInt()
                    )
                    childFileOS.write(
                        dataBuffer, 0,
                        min(dataBuffer.size.toLong(), fileSize).toInt()
                    )
                    sizeOfDataReadFromDirectoryTransfer += min(
                        dataBuffer.size.toLong(),
                        fileSize
                    ).toInt()
                    fileSize -= min(dataBuffer.size.toLong(), fileSize).toInt()
                    dataReceiveListener.onReceive(
                        originalTransferDirectoryName,
                        directorySize,
                        ((directorySize - sizeOfDataReadFromDirectoryTransfer) / directorySize.toFloat()) * 100f,
                        MediaType.Directory.value,
                        null,
                        DataToTransfer.TransferStatus.RECEIVE_ONGOING
                    )
                }
                childFileOS.close()
            }
        }
    }


    override suspend fun insertFile() {

    }

    override suspend fun getDirectoryChildren(directoryPath: String): List<DataToTransfer> {
        var children = listOf<DataToTransfer>()
        File(directoryPath).apply {
            children = listFiles()?.map {
                DataToTransfer.DeviceFile(it)
            } ?: listOf()
        }
        return children
    }
}