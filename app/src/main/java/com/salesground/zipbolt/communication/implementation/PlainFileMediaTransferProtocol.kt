package com.salesground.zipbolt.communication.implementation

import com.salesground.zipbolt.communication.MediaTransferProtocol
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.model.MediaType
import com.salesground.zipbolt.repository.SavedFilesRepository
import com.salesground.zipbolt.repository.ZipBoltSavedFilesRepository
import com.salesground.zipbolt.service.DataTransferService
import java.io.BufferedInputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.FileInputStream
import kotlin.math.min

class PlainFileMediaTransferProtocol(savedFilesRepository: SavedFilesRepository) {
    private var mTransferMetaData =
        MediaTransferProtocol.MediaTransferProtocolMetaData.KEEP_RECEIVING
    private val receiveBuffer = ByteArray(DataTransferService.BUFFER_SIZE)
    private val transferBuffer = ByteArray(DataTransferService.BUFFER_SIZE)

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
        dataOutputStream.writeInt(MediaType.File.Document.UnknownDocument.value)
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
}