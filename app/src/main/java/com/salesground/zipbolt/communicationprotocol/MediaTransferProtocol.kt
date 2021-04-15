package com.salesground.zipbolt.communicationprotocol

import java.io.DataInputStream
import java.io.DataOutputStream

interface MediaTransferProtocol {
    suspend fun transferMedia(dataOutputStream: DataOutputStream)
    suspend fun receiveMedia(dataInputStream: DataInputStream)
}