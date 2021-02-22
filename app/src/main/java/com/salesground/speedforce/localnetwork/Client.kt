package com.salesground.speedforce.localnetwork

import com.salesground.speedforce.communicationprotocol.FileTransferProtocol
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.Socket

class Client(private val serverIpAddress : String) {

   suspend fun connectToServer(parentFolder : File) {
       withContext(Dispatchers.IO) {
           val server: Socket = Socket(serverIpAddress, 8090)

           FileTransferProtocol(server).receiveFile(parentFolder = parentFolder)
       }
   }
}