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

class PlainFileMediaTransferProtocol(savedFilesRepository: SavedFilesRepository) {

    private val receiveBuffer = ByteArray(DataTransferService.BUFFER_SIZE)
    private val transferBuffer = ByteArray(DataTransferService.BUFFER_SIZE)

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
        while (unreadDataSize > 0) {

        }


    }
}