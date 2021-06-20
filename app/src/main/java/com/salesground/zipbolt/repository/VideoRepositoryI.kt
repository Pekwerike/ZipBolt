package com.salesground.zipbolt.repository

import android.content.Context
import com.salesground.zipbolt.communication.MediaTransferProtocol
import com.salesground.zipbolt.model.DataToTransfer
import java.io.DataInputStream

interface VideoRepositoryI {

    suspend fun insertVideoIntoMediaStore(
        context: Context,
        videoName: String,
        videoSize: Long,
        dataInputStream: DataInputStream,
        transferMetaDataUpdateListener: MediaTransferProtocol.TransferMetaDataUpdateListener,
        dataReceiveListener: MediaTransferProtocol.DataReceiveListener
    )

    suspend fun getVideosOnDevice(context: Context): MutableList<DataToTransfer>
}