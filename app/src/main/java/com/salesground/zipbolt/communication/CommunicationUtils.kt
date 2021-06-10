package com.salesground.zipbolt.communication

import com.salesground.zipbolt.model.DataToTransfer
import java.io.BufferedOutputStream
import java.io.DataInputStream
import java.io.File
import kotlin.math.min


fun DataInputStream.readStreamDataIntoFile(
    dataReceiveListener: MediaTransferProtocol.DataReceiveListener,
    dataDisplayName: String,
    size: Long,
    transferMetaDataUpdateListener: MediaTransferProtocol.TransferMetaDataUpdateListener,
    receivingFile: File,
    receivingFileBufferedOutputStream: BufferedOutputStream,
    dataBuffer: ByteArray,
    dataType: DataToTransfer.MediaType
): Boolean {
    var dataSize = size

    while (dataSize > 0) {
        // read the current transfer status, to determine whether to continue with the transfer
        when (readInt()) {
            MediaTransferProtocol.MediaTransferProtocolMetaData.KEEP_RECEIVING.value -> {

            }
            MediaTransferProtocol.MediaTransferProtocolMetaData.CANCEL_ACTIVE_RECEIVE.value -> {
                // delete image file
                receivingFile.delete()
                return false
            }
            MediaTransferProtocol.MediaTransferProtocolMetaData.KEEP_RECEIVING_BUT_CANCEL_ACTIVE_TRANSFER.value -> {
                transferMetaDataUpdateListener.onMetaTransferDataUpdate(
                    MediaTransferProtocol.MediaTransferProtocolMetaData.KEEP_RECEIVING_BUT_CANCEL_ACTIVE_TRANSFER
                )
            }
        }

        readFully(
            dataBuffer, 0,
            min(dataBuffer.size.toLong(), dataSize).toInt()
        )

        receivingFileBufferedOutputStream.write(
            dataBuffer,
            0, min(dataBuffer.size.toLong(), dataSize).toInt()
        )
        dataSize -= min(dataBuffer.size.toLong(), dataSize).toInt()

        dataReceiveListener.onReceive(
            dataDisplayName,
            dataSize,
            ((size - dataSize) / size.toFloat()) * 100f,
            dataType.value,
            null,
            DataToTransfer.TransferStatus.RECEIVE_ONGOING
        )
    }

    receivingFileBufferedOutputStream.close()
    return true
}