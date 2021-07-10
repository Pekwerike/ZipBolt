package com.salesground.zipbolt.repository

import com.salesground.zipbolt.communication.MediaTransferProtocol
import com.salesground.zipbolt.model.DataToTransfer
import java.io.DataInputStream

interface AudioRepository {

    suspend fun insertAudioIntoMediaStore(
        audioName: String,
        audioSize: Long,
        audioDuration: Long,
        dataInputStream: DataInputStream,
        transferMetaDataUpdateListener: MediaTransferProtocol.TransferMetaDataUpdateListener,
        dataReceiveListener: MediaTransferProtocol.DataReceiveListener
    )

    suspend fun getAudioOnDevice(): MutableList<DataToTransfer>
}