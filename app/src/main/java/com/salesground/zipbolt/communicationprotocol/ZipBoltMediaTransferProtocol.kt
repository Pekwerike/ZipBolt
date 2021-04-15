package com.salesground.zipbolt.communicationprotocol

import com.salesground.zipbolt.model.DataToTransfer
import java.io.DataInputStream
import java.io.DataOutputStream
import javax.inject.Inject

class ZipBoltMediaTransferProtocol @Inject constructor(
    private val mediaTransferProtocol: MediaTransferProtocol
) : MediaTransferProtocol {

    override suspend fun transferMedia(
        dataToTransfer: DataToTransfer,
        dataOutputStream: DataOutputStream
    ) {

    }

    override suspend fun receiveMedia(dataInputStream: DataInputStream) {

    }

}