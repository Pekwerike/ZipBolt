package com.salesground.zipbolt.localnetwork

import android.content.Context
import com.salesground.zipbolt.communicationprotocol.FileTransferProtocol
import com.salesground.zipbolt.model.ImageModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.ServerSocket
import java.net.Socket

class Server {

    suspend fun listenIncomingConnection(
        context : Context,
        filesToTransfer: MutableList<ImageModel>? = null,
        parentFolder: File? = null
    ) {
        withContext(Dispatchers.IO) {
            val serverSocket: ServerSocket = ServerSocket(8090)
            // blocking call
            val client: Socket = serverSocket.accept()
            val fileTransferProtocol = FileTransferProtocol(client)

            filesToTransfer?.let {
                fileTransferProtocol.transferFile(it, context)
            }
            parentFolder?.let {
                fileTransferProtocol.receiveFile(it)
            }
        }
    }
}