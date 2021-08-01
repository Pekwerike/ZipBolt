package com.salesground.zipbolt.communication.implementation

import com.salesground.zipbolt.communication.MediaTransferProtocol
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.service.DataTransferService
import java.io.*
import java.util.*
import kotlin.math.min

class DirectoryMediaTransferProtocol : MediaTransferProtocol {
    private val buffer = ByteArray(DataTransferService.BUFFER_SIZE)
    private var directorySize: Long = 0L
    private var dataSizeReadOutFromDirectory: Long = 0L
    override fun cancelCurrentTransfer(transferMetaData: MediaTransferProtocol.MediaTransferProtocolMetaData) {

    }

    override suspend fun transferMedia(
        dataToTransfer: DataToTransfer,
        dataOutputStream: DataOutputStream,
        dataTransferListener: MediaTransferProtocol.DataTransferListener
    ) {
        dataToTransfer as DataToTransfer.DeviceFile
        directorySize = dataToTransfer.file.getDirectorySize()
        transferDirectory(
            dataOutputStream, dataToTransfer,
            dataToTransfer.file,
            dataTransferListener
        )
    }

    private fun transferDirectory(
        dataOutputStream: DataOutputStream,
        originalDataToTransfer: DataToTransfer,
        directory: File,
        dataTransferListener: MediaTransferProtocol.DataTransferListener
    ) {
        dataOutputStream.writeUTF(directory.name)
        val directoryChildren = directory.listFiles()
        directoryChildren?.let {
            dataOutputStream.writeInt(directoryChildren.size)
            it.forEach { directoryChild ->
                if (directoryChild.isDirectory) {

                } else {
                    dataOutputStream.writeInt(DataToTransfer.MediaType.FILE.value)
                    dataOutputStream.writeUTF(directoryChild.name)
                    var directoryChildFileLength = directoryChild.length()
                    dataOutputStream.writeLong(directoryChildFileLength)
                    val fileDataInputStream = DataInputStream(
                        BufferedInputStream(FileInputStream(directoryChild))
                    )

                    dataTransferListener.onTransfer(
                        originalDataToTransfer,
                        0f,
                        DataToTransfer.TransferStatus.TRANSFER_STARTED
                    )
                    var lenghtRead: Int = 0
                    while (directoryChildFileLength > 0) {
                        fileDataInputStream.readFully(
                            buffer, 0, min(
                                directoryChildFileLength,
                                buffer.size.toLong()
                            ).toInt()
                        )
                        lenghtRead = min(directoryChildFileLength, buffer.size.toLong()).toInt()
                        directoryChildFileLength -= lenghtRead
                        dataSizeReadOutFromDirectory += lenghtRead
                        dataOutputStream.write(
                            buffer,
                            0, lenghtRead
                        )
                        directoryChildFileLength += lenghtRead

                        dataTransferListener.onTransfer(
                            originalDataToTransfer,
                            ((directorySize - dataSizeReadOutFromDirectory) / directorySize.toFloat()),
                            DataToTransfer.TransferStatus.TRANSFER_ONGOING
                        )
                    }
                }
            }
        }
    }

    override suspend fun receiveMedia(
        dataInputStream: DataInputStream,
        dataReceiveListener: MediaTransferProtocol.DataReceiveListener
    ) {

    }

    private fun File.getDirectorySize(): Long {
        var directorySize: Long = 0L
        val directoryStack: Deque<File> = ArrayDeque()
        directoryStack.push(this)
        var directory: File

        while (directoryStack.isNotEmpty()) {
            directory = directoryStack.pop()
            directory.listFiles()?.forEach {
                if (it.isDirectory) {
                    directoryStack.add(it)
                } else {
                    directorySize += it.length()
                }
            }
        }
        return directorySize
    }
}