package com.salesground.zipbolt.communicationprotocol

import android.content.Context
import com.salesground.zipbolt.model.ImageModel
import java.io.*
import java.net.Socket

class FileTransferProtocol(private val socket: Socket) {

    fun transferFile(filesToTransfer: MutableList<ImageModel>, context : Context) {
        val socketOutputStream = socket.getOutputStream()
        val socketBOS = BufferedOutputStream(socketOutputStream)
        val socketDOS = DataOutputStream(socketBOS)

        // write the number of files to transer
        socketDOS.writeInt(filesToTransfer.size)

        // write the name, length and byte of each file to transfer
        filesToTransfer.forEach { mediaModel ->
            socketDOS.writeUTF(mediaModel.imageDisplayName)
            socketDOS.writeLong(mediaModel.imageSize)

            context.contentResolver.openFileDescriptor(mediaModel.imageUri, "r")?.let { pdf ->
                val fileInputStream = FileInputStream(pdf.fileDescriptor)
                val bufferArray = ByteArray(5_000_000)
                var length : Int
                while(fileInputStream.read(bufferArray).also { length = it } != -1){
                    socketDOS.write(bufferArray, 0, length)
                }
                fileInputStream.close()
            }
        }
    }

    fun receiveFile(parentFolder: File) {
        val socketInputStream = socket.getInputStream()
        val socketBIS = BufferedInputStream(socketInputStream)
        val socketDIS = DataInputStream(socketBIS)


        // read the number of files sent
        val filesCount = socketDIS.readInt()

        // read the name and length of each file sent
        for (i in 0 until filesCount) {
            val fileName = socketDIS.readUTF()
            val fileLength = socketDIS.readLong()

            val receivedFile = File(parentFolder, fileName)
            val receivedFileOuputStream = FileOutputStream(receivedFile)
            var bytesToReadOut = fileLength
            val buffer = ByteArray(5_000_000)

            while (bytesToReadOut > 0) {
                val bytesRead =
                    socketDIS.read(buffer, 0, Math.min(bytesToReadOut.toInt(), buffer.size))
                if (bytesRead == -1) {
                    break; // end of stream
                }
                receivedFileOuputStream.write(buffer, 0, bytesRead)
                bytesToReadOut -= bytesRead
            }
            receivedFileOuputStream.close()
        }
    }
}