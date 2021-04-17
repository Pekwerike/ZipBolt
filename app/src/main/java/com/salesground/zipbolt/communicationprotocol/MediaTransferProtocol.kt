package com.salesground.zipbolt.communicationprotocol

import com.salesground.zipbolt.model.DataToTransfer
import java.io.DataInputStream
import java.io.DataOutputStream

interface MediaTransferProtocol {

    enum class TransferState {
        RECEIVING,
        TRANSFERING
    }

    interface MediaTransferListener {
        fun percentageOfBytesTransferred(
            bytesTransferred: Pair<String, Float>,
            transferState: TransferState
        )
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