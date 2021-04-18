package com.salesground.zipbolt.communicationprotocol

import com.salesground.zipbolt.model.DataToTransfer
import java.io.DataInputStream
import java.io.DataOutputStream

interface MediaTransferProtocol {

    enum class TransferMetaData(val status: String) {

        CANCEL_ACTIVE_RECEIVE("CancelActiveReceive"),
        CANCEL_ACTIVE_TRANSTER("CancelActiveTransfer"),
        KEEP_RECEIVING("KeepReceiving"),
        KEE_RECEIVING_BUT_CANCEL_ACTIVE_TRANSFER("KeepReceivingButCancelActiveTransfer"),
        PAUSE_ACTIVE_TRANSFER("PauseActiveTransfer")
    }

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

    fun setDataFlowListener(dataFlowListener: (Pair<String, Float>, TransferState) -> Unit)

    fun cancelCurrentTransfer(transferMetaData: TransferMetaData)


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