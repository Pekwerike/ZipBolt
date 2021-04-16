package com.salesground.zipbolt.communicationprotocol

import com.salesground.zipbolt.model.DataToTransfer
import java.io.DataInputStream
import java.io.DataOutputStream

interface MediaTransferProtocol {

    interface MediaTransferListener {
        fun percentageOfBytesTransferred(bytesTransferred: Float)
    }

    fun setMediaTransferListener(
        mediaTransferListener:
        MediaTransferListener
    )

    suspend fun transferMedia(
        dataToTransfer: DataToTransfer,
        dataOutputStream: DataOutputStream
    )

    suspend fun receiveMedia(dataInputStream: DataInputStream)
}