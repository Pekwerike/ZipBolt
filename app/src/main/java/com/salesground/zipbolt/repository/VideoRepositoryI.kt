package com.salesground.zipbolt.repository

import android.net.Uri
import com.salesground.zipbolt.communication.MediaTransferProtocol
import com.salesground.zipbolt.model.DataToTransfer
import java.io.DataInputStream

interface VideoRepositoryI {

    suspend fun insertVideoIntoMediaStore(
        videoName: String,
        videoSize: Long,
        videoDuration: Long,
        dataInputStream: DataInputStream,
        transferMetaDataUpdateListener: MediaTransferProtocol.TransferMetaDataUpdateListener,
        dataReceiveListener: MediaTransferProtocol.DataReceiveListener
    )

    suspend fun getVideosOnDevice(): MutableList<DataToTransfer>

    suspend fun getVideoDuration(videoUri: Uri): Long
}