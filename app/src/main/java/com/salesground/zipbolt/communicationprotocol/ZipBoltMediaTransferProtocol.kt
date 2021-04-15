package com.salesground.zipbolt.communicationprotocol

import android.content.Context
import com.salesground.zipbolt.model.DataToTransfer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.DataInputStream
import java.io.DataOutputStream
import javax.inject.Inject

class ZipBoltMediaTransferProtocol @Inject constructor(
    @ApplicationContext private val context : Context,
    private val mediaTransferProtocol: MediaTransferProtocol
) : MediaTransferProtocol {

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun transferMedia(
        dataToTransfer: DataToTransfer,
        dataOutputStream: DataOutputStream
    ) {
        withContext(Dispatchers.IO) {
            dataOutputStream.writeUTF(dataToTransfer.dataDisplayName)
            dataOutputStream.writeLong(dataToTransfer.dataSize)
            dataOutputStream.writeUTF(dataToTransfer.dataType)


        }
    }

    override suspend fun receiveMedia(dataInputStream: DataInputStream) {

    }

}