package com.salesground.zipbolt.repository

import com.salesground.zipbolt.communication.MediaTransferProtocol
import com.salesground.zipbolt.model.DataToTransfer
import java.io.DataInputStream

interface VideoRepositoryI {

    suspend fun insertVideoIntoMediaStore(
        videoName: String,
        videoSize: Long,
        dataInputStream: DataInputStream,
        transferMetaDataUpdateListener: MediaTransferProtocol.TransferMetaDataUpdateListener,
        dataReceiveListener: MediaTransferProtocol.DataReceiveListener
    )

    suspend fun getVideosOnDevice(): MutableList<DataToTransfer>
}