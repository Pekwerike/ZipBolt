package com.salesground.zipbolt.communication.implementation

import com.salesground.zipbolt.communication.MediaTransferProtocol
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.model.MediaType
import com.salesground.zipbolt.repository.SavedFilesRepository
import com.salesground.zipbolt.repository.ZipBoltSavedFilesRepository
import com.salesground.zipbolt.service.DataTransferService
import java.io.*
import kotlin.math.min

class PlainFileMediaTransferProtocol(savedFilesRepository: SavedFilesRepository) {
    private var mTransferMetaData =
        MediaTransferProtocol.MediaTransferProtocolMetaData.KEEP_RECEIVING
    private val receiveBuffer = ByteArray(DataTransferService.BUFFER_SIZE)
    private val transferBuffer = ByteArray(DataTransferService.BUFFER_SIZE)
    private val zipBoltDocumentsFolder by lazy {
        savedFilesRepository.getZipBoltMediaCategoryBaseDirectory(SavedFilesRepository.ZipBoltMediaCategory.DOCUMENTS_BASE_DIRECTORY)
    }

    fun cancelCurrentTransfer(transferMetaData: MediaTransferProtocol.MediaTransferProtocolMetaData) {
        mTransferMetaData = transferMetaData
    }

    private fun resetTransferMetaData(
        dataTransferListener: MediaTransferProtocol.DataTransferListener,
        dataToTransfer: DataToTransfer
    ) {
        dataTransferListener.onTransfer(
            dataToTransfer,
            0f,
            DataToTransfer.TransferStatus.TRANSFER_CANCELLED
        )
        // return the mTransferMetaData to keep receiving incase of reuse
        mTransferMetaData = MediaTransferProtocol.MediaTransferProtocolMetaData.KEEP_RECEIVING
    }

    fun transferFile(
        dataToTransfer: DataToTransfer,
        dataOutputStream: DataOutputStream,
        dataTransferListener: MediaTransferProtocol.DataTransferListener
    ) {
        dataToTransfer as DataToTransfer.DeviceFile

        // send file name, size, and type
        dataOutputStream.writeInt(dataToTransfer.dataType)
        dataOutputStream.writeUTF(dataToTransfer.file.name)
        dataOutputStream.writeLong(dataToTransfer.dataSize)
        var unreadDataSize = dataToTransfer.dataSize
        val fileDataInputStream = DataInputStream(
            BufferedInputStream(
                FileInputStream(dataToTransfer.file)
            )
        )
        dataTransferListener.onTransfer(
            dataToTransfer,
            0f,
            DataToTransfer.TransferStatus.TRANSFER_STARTED
        )
        var lengthRead: Int
        while (unreadDataSize > 0) {
            lengthRead = min(unreadDataSize, transferBuffer.size.toLong()).toInt()
            fileDataInputStream.readFully(
                transferBuffer, 0, lengthRead
            )
            //*** write the current transfer state to receiver
            dataOutputStream.writeInt(mTransferMetaData.value)

            if (mTransferMetaData == MediaTransferProtocol.MediaTransferProtocolMetaData.CANCEL_ACTIVE_RECEIVE) {
                resetTransferMetaData(dataTransferListener, dataToTransfer)
                return
            } else if (mTransferMetaData == MediaTransferProtocol.MediaTransferProtocolMetaData.KEEP_RECEIVING_BUT_CANCEL_ACTIVE_TRANSFER) {
                mTransferMetaData =
                    MediaTransferProtocol.MediaTransferProtocolMetaData.KEEP_RECEIVING
            }

            dataOutputStream.write(
                transferBuffer,
                0, lengthRead
            )

            unreadDataSize -= lengthRead

            dataTransferListener.onTransfer(
                dataToTransfer,
                ((dataToTransfer.dataSize - unreadDataSize) / dataToTransfer.dataSize.toFloat()) * 100f,
                DataToTransfer.TransferStatus.TRANSFER_ONGOING
            )
        }
        fileDataInputStream.close()
        dataTransferListener.onTransfer(
            dataToTransfer,
            100f,
            DataToTransfer.TransferStatus.TRANSFER_COMPLETE
        )
    }

    fun receivePlainFile(
        dataInputStream: DataInputStream,
        dataReceiveListener: MediaTransferProtocol.DataReceiveListener,
        transferMetaDataUpdateListener: MediaTransferProtocol.TransferMetaDataUpdateListener,
        dataType: Int
    ) {
        // read file name and size
        val fileName = dataInputStream.readUTF()
        val fileSize = dataInputStream.readLong()
        var fileSizeUnread = fileSize

        // create file and open output stream
        val plainFileBufferedOutputStream = BufferedOutputStream(
            FileOutputStream(File(zipBoltDocumentsFolder, fileName))
        )
        dataReceiveListener.onReceive(
            fileName,
            fileSize,
            0f,
            dataType,
            null,
            DataToTransfer.TransferStatus.RECEIVE_STARTED
        )

        while (fileSizeUnread > 0) {

        }

    }

}