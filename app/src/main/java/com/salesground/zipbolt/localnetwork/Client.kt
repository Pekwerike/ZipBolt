package com.salesground.zipbolt.localnetwork

import android.content.Context
import com.salesground.zipbolt.communicationprotocol.FileTransferProtocol
import com.salesground.zipbolt.model.ImageModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.Socket

class Client(private val serverIpAddress: String) {

    private lateinit var fileTransferProtocol: FileTransferProtocol

    suspend fun connectToServer(
        context: Context,
        filesToTransfer: MutableList<ImageModel>? = null,
        parentFolder: File? = null
    ) {
        withContext(Dispatchers.IO) {
            val server: Socket = Socket(serverIpAddress, 8090)
            fileTransferProtocol = FileTransferProtocol(server)

            filesToTransfer?.let {
                fileTransferProtocol.transferFile(it, context)
            }
            parentFolder?.let {
                fileTransferProtocol.receiveFile(it)
            }
        }
    }


}