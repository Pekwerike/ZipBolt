package com.salesground.speedforce.localnetwork

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.ServerSocket
import java.net.Socket

class Server {

    suspend fun listenIncomingConnection() {
        withContext(Dispatchers.IO) {
            val serverSocket: ServerSocket = ServerSocket(8090)
            // blocking call
            val client: Socket = serverSocket.accept()
        }
    }
}