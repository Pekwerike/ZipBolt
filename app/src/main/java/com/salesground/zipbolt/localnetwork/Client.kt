package com.salesground.zipbolt.localnetwork

import android.content.Context
import com.salesground.zipbolt.model.MediaModel
import java.io.File
import java.net.InetSocketAddress
import java.net.Socket

class Client(private val serverIpAddress: String) {


    suspend fun connectToServer(
        context: Context,
        filesToTransfer: MutableList<MediaModel>? = null,
        parentFolder: File? = null
    ) {
            val server = Socket()
            server.bind(null)
            server.connect(InetSocketAddress(serverIpAddress, 8090), 100000)

        }

}