package com.salesground.speedforce.localnetwork

import com.salesground.speedforce.communicationprotocol.FileTransferProtocol
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.ServerSocket
import java.net.Socket

class Server {

    suspend fun listenIncomingConnection(
        filesToTransfer: MutableList<File>? = null,
        parentFolder: File? = null
    ) {
        withContext(Dispatchers.IO) {
            val serverSocket: ServerSocket = ServerSocket(8090)
            // blocking call
            val client: Socket = serverSocket.accept()
            val fileTransferProtocol = FileTransferProtocol(client)

            filesToTransfer?.let {
                fileTransferProtocol.transferFile(it)
            }
            parentFolder?.let {
                fileTransferProtocol.receiveFile(it)
            }
        }
    }
}