package com.salesground.zipbolt.communicationprotocol

import android.net.Uri
import com.salesground.zipbolt.model.DataToTransfer
import java.io.DataInputStream
import java.io.DataOutputStream

interface MediaTransferProtocol {

    enum class TransferMetaData(val status: String) {

        CANCEL_ACTIVE_RECEIVE("CancelActiveReceive"),
        CANCEL_ACTIVE_TRANSTER("CancelActiveTransfer"),
        KEEP_RECEIVING("KeepReceiving"),
        KEEP_RECEIVING_BUT_CANCEL_ACTIVE_TRANSFER("KeepReceivingButCancelActiveTransfer"),
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


    fun cancelCurrentTransfer(transferMetaData: TransferMetaData)


    suspend fun transferMedia(
        dataToTransfer: DataToTransfer,
        dataOutputStream: DataOutputStream,
        dataTransferListener: (
            displayName: String, dataSize: Long, percentTransferred: Float,
            transferState: TransferState
        ) -> Unit
    )

    suspend fun receiveMedia(
        dataInputStream: DataInputStream,
        bytesReceivedListener: (
            dataDisplayName: String, dataSize: Long,  percentageOfDataRead: Float, dataType: String,
            dataUri: Uri
        ) -> Unit
    )

    fun writeFileMetaData(
        dataOutputStream: DataOutputStream,
        dataToTransfer: DataToTransfer
    )

    fun readFileMetaData(dataInputStream: DataInputStream): Triple<String, Long, String>
}