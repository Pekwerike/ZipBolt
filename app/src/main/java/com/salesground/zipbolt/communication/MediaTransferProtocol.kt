package com.salesground.zipbolt.communication

import android.net.Uri
import com.salesground.zipbolt.model.DataToTransfer
import java.io.DataInputStream
import java.io.DataOutputStream

interface MediaTransferProtocol {


    enum class MediaTransferProtocolMetaData(val value: Int) {
        NO_DATA(200),
        DATA_AVAILABLE(201),
        CANCEL_ON_GOING_TRANSFER(203),

        CANCEL_ACTIVE_RECEIVE(204),
        CANCEL_ACTIVE_TRANSFER(205),
        KEEP_RECEIVING(206),
        KEEP_RECEIVING_BUT_CANCEL_ACTIVE_TRANSFER(207),
        PAUSE_ACTIVE_TRANSFER(208)
    }

    fun cancelCurrentTransfer(transferMetaData: MediaTransferProtocolMetaData)

    suspend fun transferMedia(
        dataToTransfer: DataToTransfer,
        dataOutputStream: DataOutputStream,
        dataTransferListener: DataTransferListener
    )


    suspend fun receiveMedia(
        dataInputStream: DataInputStream,
        dataReceiveListener: DataReceiveListener
    )

    interface DataTransferListener {
        fun onTransfer(
            dataToTransfer: DataToTransfer,
            percentTransferred: Float,
            transferStatus: DataToTransfer.TransferStatus
        )
    }

    interface DataReceiveListener {
        fun onReceive(
            dataDisplayName: String, dataSize: Long, percentageOfDataRead: Float, dataType: Int,
            dataUri: Uri?, dataTransferStatus: DataToTransfer.TransferStatus
        )
    }

    interface TransferMetaDataUpdateListener {
        fun onMetaTransferDataUpdate(mediaTransferProtocolMetaData: MediaTransferProtocolMetaData)
    }

}