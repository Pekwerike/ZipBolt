package com.salesground.speedforce.communicationprotocol

import androidx.collection.ArrayMap
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

        // write the byte of each file to the socketDOS
        filesToTransfer.forEach { file: File ->
            val fileInputStream = FileInputStream(file)
            val buffer = ByteArray(1_000_000)
            var length: Int
            while (fileInputStream.read(buffer).also {
                    length = it
                } != -1) {
                socketDOS.write(buffer, 0,length)
            }
        }
    }

    fun receiveFile() {
        val socketInputStream = socket.getInputStream()
        val socketBIS = BufferedInputStream(socketInputStream)
        val socketDIS = DataInputStream(socketBIS)
        val filesReceived: ArrayMap<String, Long> = ArrayMap()

        // read the number of files sent
        val filesCount = socketDIS.readInt()

        // read the name and length of each file sent
        for (i in 0 until filesCount) {
            val fileName = socketDIS.readUTF()
            val fileLength = socketDIS.readLong()
            filesReceived.put(fileName, fileLength)
        }

    }
}