package com.salesground.zipbolt.repository

import android.content.pm.ApplicationInfo
import com.salesground.zipbolt.communication.MediaTransferProtocol
import com.salesground.zipbolt.model.ApplicationModel
import com.salesground.zipbolt.model.DataToTransfer
import java.io.DataInputStream

interface ApplicationsRepositoryInterface {
    suspend fun getAllAppsOnDevice(): MutableList<ApplicationInfo>
    suspend fun getNonSystemAppsOnDevice(): List<DataToTransfer>
    suspend fun insertApplicationIntoDevice(
        appFileName: String,
        appSize: Long,
        dataInputStream: DataInputStream,
        transferMetaDataUpdateListener: MediaTransferProtocol.TransferMetaDataUpdateListener,
        dataReceiveListener: MediaTransferProtocol.DataReceiveListener
    )
}