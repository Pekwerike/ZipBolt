package com.salesground.speedforce.localnetwork

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.Socket

class Client(private val serverIpAddress : String) {

   suspend fun connectToServer() {
       withContext(Dispatchers.IO) {
           val server: Socket = Socket(serverIpAddress, 8090)

       }
   }
}