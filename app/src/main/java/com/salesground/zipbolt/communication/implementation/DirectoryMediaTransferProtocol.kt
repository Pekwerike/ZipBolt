package com.salesground.zipbolt.communication.implementation

import android.net.Uri
import android.util.Log
import com.salesground.zipbolt.communication.MediaTransferProtocol
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.model.MediaType
import com.salesground.zipbolt.repository.SavedFilesRepository
import com.salesground.zipbolt.service.DataTransferService
import com.salesground.zipbolt.utils.getDirectorySize
import java.io.*
import kotlin.math.min

class DirectoryMediaTransferProtocol(
    private val savedFilesRepository: SavedFilesRepository
) {
    private var mTransferMetaData =
        MediaTransferProtocol.MediaTransferProtocolMetaData.KEEP_RECEIVING
    private val transferBuffer = ByteArray(DataTransferService.BUFFER_SIZE)
    private val receiveBuffer = ByteArray(DataTransferService.BUFFER_SIZE)
    private var dataSizeUnreadFromDirectory: Long = 0L
    private var dataSizeUnreadFromSocket: Long = 0L
    private val zipBoltBaseFolderDirectory: File by lazy {
        savedFilesRepository.getZipBoltMediaCategoryBaseDirectory(SavedFilesRepository.ZipBoltMediaCategory.FOLDERS_BASE_DIRECTORY)
    }

    fun cancelCurrentTransfer(transferMetaData: MediaTransferProtocol.MediaTransferProtocolMetaData) {
        mTransferMetaData = transferMetaData
    }

    fun resetTransferMetaData() {
        mTransferMetaData = MediaTransferProtocol.MediaTransferProtocolMetaData.KEEP_RECEIVING
    }

    fun transferMedia(
        dataToTransfer: DataToTransfer,
        dataOutputStream: DataOutputStream,
        dataTransferListener: MediaTransferProtocol.DataTransferListener
    ) {
        dataToTransfer as DataToTransfer.DeviceFile
        dataToTransfer.dataSize = dataToTransfer.file.getDirectorySize()
        dataSizeUnreadFromDirectory = dataToTransfer.dataSize

        // send directory name, size, and type
        dataOutputStream.writeInt(MediaType.File.Directory.value)
        dataOutputStream.writeUTF(dataToTransfer.file.name)
        dataOutputStream.writeLong(dataToTransfer.dataSize)

        val directoryChildren = dataToTransfer.file.listFiles()
        if (directoryChildren != null) {
            dataOutputStream.writeInt(directoryChildren.size)
            for (directoryChild in directoryChildren) {
                if (directoryChild.isDirectory) {
                    dataOutputStream.writeInt(MediaType.File.Directory.value)
                    if (!transferDirectory(
                            dataOutputStream,
                            dataToTransfer,
                            directoryChild,
                            dataTransferListener
                        )
                    ) {
                        resetTransferMetaData()
                        return
                    }
                } else {
                    dataOutputStream.writeInt(MediaType.File.Document.UnknownDocument.value)
                    dataOutputStream.writeUTF(directoryChild.name)
                    var directoryChildFileLength = directoryChild.length()
                    dataOutputStream.writeLong(directoryChildFileLength)
                    val fileDataInputStream = DataInputStream(
                        BufferedInputStream(FileInputStream(directoryChild))
                    )

                    dataTransferListener.onTransfer(
                        dataToTransfer,
                        ((dataToTransfer.dataSize - dataSizeUnreadFromDirectory) / dataToTransfer.dataSize.toFloat()) * 100f,
                        DataToTransfer.TransferStatus.TRANSFER_STARTED
                    )
                    var lengthRead: Int
                    while (directoryChildFileLength > 0) {

                        lengthRead =
                            min(directoryChildFileLength, transferBuffer.size.toLong()).toInt()

                        fileDataInputStream.readFully(
                            transferBuffer, 0, lengthRead
                        )
                        //*** write the current transfer state to receiver
                        dataOutputStream.writeInt(mTransferMetaData.value)

                        if (mTransferMetaData == MediaTransferProtocol.MediaTransferProtocolMetaData.CANCEL_ACTIVE_RECEIVE) {
                            resetTransferMetaData()
                            return
                        } else if (mTransferMetaData == MediaTransferProtocol.MediaTransferProtocolMetaData.KEEP_RECEIVING_BUT_CANCEL_ACTIVE_TRANSFER) {
                            mTransferMetaData =
                                MediaTransferProtocol.MediaTransferProtocolMetaData.KEEP_RECEIVING
                        }

                        dataOutputStream.write(
                            transferBuffer,
                            0, lengthRead
                        )
                        directoryChildFileLength -= lengthRead
                        dataSizeUnreadFromDirectory -= lengthRead

                        dataTransferListener.onTransfer(
                            dataToTransfer,
                            ((dataToTransfer.dataSize - dataSizeUnreadFromDirectory) / dataToTransfer.dataSize.toFloat()) * 100f,
                            DataToTransfer.TransferStatus.TRANSFER_ONGOING
                        )
                    }
                    fileDataInputStream.close()
                }
            }
        }
        dataTransferListener.onTransfer(
            dataToTransfer,
            100f,
            DataToTransfer.TransferStatus.TRANSFER_COMPLETE
        )
    }

    // returns true if directory transfer was not cancelled by user
    private fun transferDirectory(
        dataOutputStream: DataOutputStream,
        originalDataToTransfer: DataToTransfer,
        directory: File,
        dataTransferListener: MediaTransferProtocol.DataTransferListener,
    ): Boolean {
        dataOutputStream.writeUTF(directory.name)
        val directoryChildren = directory.listFiles()
        if (directoryChildren != null) {
            dataOutputStream.writeInt(directoryChildren.size)
            for (directoryChild in directoryChildren) {
                if (directoryChild.isDirectory) {
                    dataOutputStream.writeInt(MediaType.File.Directory.value)
                    return transferDirectory(
                        dataOutputStream,
                        originalDataToTransfer,
                        directoryChild,
                        dataTransferListener
                    )
                } else {
                    // write file type, name, and length
                    dataOutputStream.writeInt(MediaType.File.Document.UnknownDocument.value)
                    dataOutputStream.writeUTF(directoryChild.name)
                    var directoryChildFileLength = directoryChild.length()
                    dataOutputStream.writeLong(directoryChildFileLength)

                    val fileDataInputStream = DataInputStream(
                        BufferedInputStream(FileInputStream(directoryChild))
                    )

                    dataTransferListener.onTransfer(
                        originalDataToTransfer,
                        ((originalDataToTransfer.dataSize - dataSizeUnreadFromDirectory) / originalDataToTransfer.dataSize.toFloat()) * 100f,
                        DataToTransfer.TransferStatus.TRANSFER_STARTED
                    )

                    var lengthRead: Int
                    while (directoryChildFileLength > 0) {
                        lengthRead =
                            min(directoryChildFileLength, transferBuffer.size.toLong()).toInt()
                        fileDataInputStream.readFully(
                            transferBuffer, 0, lengthRead
                        )

                        //*** write the current transfer state to receiver
                        dataOutputStream.writeInt(mTransferMetaData.value)

                        if (mTransferMetaData == MediaTransferProtocol.MediaTransferProtocolMetaData.CANCEL_ACTIVE_RECEIVE) {
                            return false
                        } else if (mTransferMetaData == MediaTransferProtocol.MediaTransferProtocolMetaData.KEEP_RECEIVING_BUT_CANCEL_ACTIVE_TRANSFER) {
                            mTransferMetaData =
                                MediaTransferProtocol.MediaTransferProtocolMetaData.KEEP_RECEIVING
                        }

                        dataOutputStream.write(
                            transferBuffer,
                            0, lengthRead
                        )

                        directoryChildFileLength -= lengthRead
                        dataSizeUnreadFromDirectory -= lengthRead

                        dataTransferListener.onTransfer(
                            originalDataToTransfer,
                            ((originalDataToTransfer.dataSize - dataSizeUnreadFromDirectory) / originalDataToTransfer.dataSize.toFloat()) * 100f,
                            DataToTransfer.TransferStatus.TRANSFER_ONGOING
                        )
                    }
                    fileDataInputStream.close()
                }
            }
        }
        return true
    }

    fun receiveMedia(
        dataInputStream: DataInputStream,
        dataReceiveListener: MediaTransferProtocol.DataReceiveListener,
        transferMetaDataUpdateListener: MediaTransferProtocol.TransferMetaDataUpdateListener,
        initialDirectoryName: String,
        initialDirectorySize: Long
    ) {
        dataSizeUnreadFromSocket = initialDirectorySize
        // create directory file in base folders directory
        val directoryFile = File(zipBoltBaseFolderDirectory, initialDirectoryName)
        directoryFile.mkdirs()
        val directoryChildrenCount = dataInputStream.readInt()
        for (i in 0 until directoryChildrenCount) {
            // read child type
            val childType = dataInputStream.readInt()
            if (childType == MediaType.File.Directory.value) {
                if (!receiveDirectory(
                        dataInputStream,
                        dataReceiveListener,
                        transferMetaDataUpdateListener,
                        directoryFile,
                        initialDirectoryName,
                        initialDirectorySize
                    )
                ) {
                    directoryFile.deleteRecursively()
                    return
                }
            } else {
                // read file name and size
                val fileName = dataInputStream.readUTF()
                var fileSize = dataInputStream.readLong()

                val directoryChild = File(directoryFile, fileName)

                val directoryChildBufferedOS = BufferedOutputStream(
                    FileOutputStream(
                        directoryChild
                    )
                )
                while (fileSize > 0) {
                    // read the current transfer status, to determine whether to continue with the transfer
                    when (dataInputStream.readInt()) {
                        MediaTransferProtocol.MediaTransferProtocolMetaData.KEEP_RECEIVING.value -> {
                        }
                        MediaTransferProtocol.MediaTransferProtocolMetaData.CANCEL_ACTIVE_RECEIVE.value -> {
                            // delete file
                            directoryFile.deleteRecursively()
                            return
                        }
                        MediaTransferProtocol.MediaTransferProtocolMetaData.KEEP_RECEIVING_BUT_CANCEL_ACTIVE_TRANSFER.value -> {
                            transferMetaDataUpdateListener.onMetaTransferDataUpdate(
                                MediaTransferProtocol.MediaTransferProtocolMetaData.KEEP_RECEIVING_BUT_CANCEL_ACTIVE_TRANSFER
                            )
                        }
                    }
                    dataInputStream.readFully(
                        receiveBuffer, 0,
                        min(receiveBuffer.size.toLong(), fileSize).toInt()
                    )
                    directoryChildBufferedOS.write(
                        receiveBuffer, 0,
                        min(receiveBuffer.size.toLong(), fileSize).toInt()
                    )
                    dataSizeUnreadFromSocket -= min(
                        receiveBuffer.size.toLong(),
                        fileSize
                    )
                    fileSize -= min(
                        receiveBuffer.size.toLong(),
                        fileSize
                    )
                    dataReceiveListener.onReceive(
                        initialDirectoryName,
                        initialDirectorySize,
                        ((initialDirectorySize - dataSizeUnreadFromSocket) / initialDirectorySize.toFloat()) * 100f,
                        MediaType.File.Directory.value,
                        null,
                        DataToTransfer.TransferStatus.RECEIVE_ONGOING
                    )
                }
                directoryChildBufferedOS.close()
            }
        }
        dataReceiveListener.onReceive(
            initialDirectoryName,
            initialDirectorySize,
            100f,
            MediaType.File.Directory.value,
            Uri.fromFile(directoryFile),
            DataToTransfer.TransferStatus.RECEIVE_COMPLETE
        )
    }

    // returns true if directory transfer was not cancelled by peer
    private fun receiveDirectory(
        dataInputStream: DataInputStream,
        dataReceiveListener: MediaTransferProtocol.DataReceiveListener,
        transferMetaDataUpdateListener: MediaTransferProtocol.TransferMetaDataUpdateListener,
        parentDirectory: File,
        initialDirectoryName: String,
        initialDirectorySize: Long
    ): Boolean {
        // read the directory name and child count
        val directoryName = dataInputStream.readUTF()
        Log.i("DirectoryName", directoryName)
        val directoryChildCount = dataInputStream.readInt()
        val directoryFile = File(parentDirectory, directoryName)
        directoryFile.mkdirs()

        for (i in 0 until directoryChildCount) {
            // read child type
            if (dataInputStream.readInt() == MediaType.File.Directory.value) {
                return receiveDirectory(
                    dataInputStream,
                    dataReceiveListener,
                    transferMetaDataUpdateListener,
                    directoryFile,
                    initialDirectoryName,
                    initialDirectorySize
                )
            } else {
                // read file name  and length
                val directoryChildFileName = dataInputStream.readUTF()
                var directoryChildFileSize = dataInputStream.readLong()

                val directoryChildFile = File(
                    directoryFile,
                    directoryChildFileName
                )
                val directoryChildFileBOS =
                    BufferedOutputStream(FileOutputStream(directoryChildFile))
                var readSize: Int
                while (directoryChildFileSize > 0) {
                    // read current receive state
                    when (dataInputStream.readInt()) {
                        MediaTransferProtocol.MediaTransferProtocolMetaData.KEEP_RECEIVING.value -> {
                        }
                        MediaTransferProtocol.MediaTransferProtocolMetaData.CANCEL_ACTIVE_RECEIVE.value -> {
                            // delete file
                            return false
                        }
                        MediaTransferProtocol.MediaTransferProtocolMetaData.KEEP_RECEIVING_BUT_CANCEL_ACTIVE_TRANSFER.value -> {
                            transferMetaDataUpdateListener.onMetaTransferDataUpdate(
                                MediaTransferProtocol.MediaTransferProtocolMetaData.KEEP_RECEIVING_BUT_CANCEL_ACTIVE_TRANSFER
                            )
                        }
                    }
                    readSize = min(
                        receiveBuffer.size.toLong(),
                        directoryChildFileSize
                    ).toInt()
                    dataInputStream.readFully(
                        receiveBuffer,
                        0, readSize
                    )
                    directoryChildFileBOS.write(
                        receiveBuffer,
                        0, readSize
                    )

                    directoryChildFileSize -= readSize
                    dataSizeUnreadFromSocket -= readSize
                    dataReceiveListener.onReceive(
                        initialDirectoryName,
                        initialDirectorySize,
                        ((initialDirectorySize - dataSizeUnreadFromSocket) / initialDirectorySize.toFloat()) * 100f,
                        MediaType.File.Directory.value,
                        null,
                        DataToTransfer.TransferStatus.RECEIVE_ONGOING
                    )
                }
                directoryChildFileBOS.close()
            }
        }
        return true
    }
}