package com.salesground.zipbolt.localnetwork

import com.salesground.zipbolt.communicationprotocol.FileTransferProtocol
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.Socket

class Client(private val serverIpAddress: String) {

    private lateinit var fileTransferProtocol : FileTransferProtocol

    suspend fun connectToServer(filesToTransfer: MutableList<File>? = null, parentFolder: File? = null) {
        withContext(Dispatchers.IO) {
            val server: Socket = Socket(serverIpAddress, 8090)
            fileTransferProtocol = FileTransferProtocol(server)

            filesToTransfer?.let {
                fileTransferProtocol.transferFile(it)
            }
            parentFolder?.let {
                fileTransferProtocol.receiveFile(it)
            }
        }
    }


}