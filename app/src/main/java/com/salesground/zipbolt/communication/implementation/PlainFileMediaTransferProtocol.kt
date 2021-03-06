package com.salesground.zipbolt.communication.implementation

import android.net.Uri
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
            //*** write the current transfer state to receiver
            dataOutputStream.writeInt(mTransferMetaData.value)

            if (mTransferMetaData == MediaTransferProtocol.MediaTransferProtocolMetaData.CANCEL_ACTIVE_RECEIVE) {
                resetTransferMetaData(dataTransferListener, dataToTransfer)
                return
            } else if (mTransferMetaData == MediaTransferProtocol.MediaTransferProtocolMetaData.KEEP_RECEIVING_BUT_CANCEL_ACTIVE_TRANSFER) {
                mTransferMetaData =
                    MediaTransferProtocol.MediaTransferProtocolMetaData.KEEP_RECEIVING
            }

            lengthRead = min(unreadDataSize, transferBuffer.size.toLong()).toInt()
            fileDataInputStream.readFully(
                transferBuffer, 0, lengthRead
            )

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
        dataType: Int,
        fileName: String,
        fileSize: Long
    ) {
        var fileSizeUnread = fileSize
        val plainFile = File(zipBoltDocumentsFolder, fileName)
        // create file and open output stream
        val plainFileBufferedOutputStream = BufferedOutputStream(
            FileOutputStream(plainFile)
        )
        dataReceiveListener.onReceive(
            fileName,
            fileSize,
            0f,
            dataType,
            null,
            DataToTransfer.TransferStatus.RECEIVE_STARTED
        )
        var lengthRead: Int
        while (fileSizeUnread > 0) {
            // read current receive state
            when (dataInputStream.readInt()) {
                MediaTransferProtocol.MediaTransferProtocolMetaData.KEEP_RECEIVING.value -> {

                }
                MediaTransferProtocol.MediaTransferProtocolMetaData.CANCEL_ACTIVE_RECEIVE.value -> {
                    // delete file
                    plainFile.delete()
                    return
                }
                MediaTransferProtocol.MediaTransferProtocolMetaData.KEEP_RECEIVING_BUT_CANCEL_ACTIVE_TRANSFER.value -> {
                    transferMetaDataUpdateListener.onMetaTransferDataUpdate(
                        MediaTransferProtocol.MediaTransferProtocolMetaData.KEEP_RECEIVING_BUT_CANCEL_ACTIVE_TRANSFER
                    )
                }
            }
            lengthRead = min(
                receiveBuffer.size.toLong(),
                fileSizeUnread
            ).toInt()
            dataInputStream.readFully(receiveBuffer, 0, lengthRead)
            plainFileBufferedOutputStream.write(receiveBuffer, 0, lengthRead)
            fileSizeUnread -= lengthRead

            dataReceiveListener.onReceive(
                fileName,
                fileSize,
                ((fileSize - fileSizeUnread) / fileSize.toFloat()) * 100f,
                dataType,
                null,
                DataToTransfer.TransferStatus.RECEIVE_ONGOING
            )
        }
        plainFileBufferedOutputStream.close()

        dataReceiveListener.onReceive(
            fileName,
            fileSize,
            100f,
            dataType,
            Uri.fromFile(plainFile),
            DataToTransfer.TransferStatus.RECEIVE_COMPLETE
        )
    }
}