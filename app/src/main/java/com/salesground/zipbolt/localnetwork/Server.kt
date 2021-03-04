package com.salesground.zipbolt.localnetwork

import android.content.Context
import com.salesground.zipbolt.model.MediaModel
import java.io.File
import java.net.ServerSocket
import java.net.Socket

class Server {

    suspend fun listenIncomingConnection(
        context : Context,
        filesToTransfer: MutableList<MediaModel>? = null,
        parentFolder: File? = null
    ) {
            val serverSocket: ServerSocket = ServerSocket(8090)

            // blocking call
            val client: Socket = serverSocket.accept()

        }
}