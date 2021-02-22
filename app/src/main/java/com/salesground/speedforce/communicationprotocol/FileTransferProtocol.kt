package com.salesground.speedforce.communicationprotocol

import java.io.*
import java.net.Socket

class FileTransferProtocol(private val socket: Socket) {

    fun transferFile(filesToTransfer: MutableList<File>) {
        val socketOutputStream = socket.getOutputStream()
        val socketBOS = BufferedOutputStream(socketOutputStream)
        val socketDOS = DataOutputStream(socketBOS)

        // write the number of files to transer
        socketDOS.writeInt(filesToTransfer.size)

        // write the name and length of each file to transfer
        filesToTransfer.forEach { file ->
            socketDOS.writeUTF(file.name)
            socketDOS.writeLong(file.length())
        }
    }

    fun receiveFile() {

    }
}