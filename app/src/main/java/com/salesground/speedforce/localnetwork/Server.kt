package com.salesground.speedforce.localnetwork

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.ServerSocket

class Server {

    suspend fun listenIncomingConnection() {
        withContext(Dispatchers.IO) {
            val serverSocket: ServerSocket = ServerSocket(8090)
            // blocking call
            val client = serverSocket.accept()
        }
    }
}