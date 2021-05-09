package com.salesground.zipbolt.communication

import java.io.*
import java.net.Socket
import kotlin.math.min

/*
 Communication algorithm created by P.C. Ekwerike
 Graphical representation of how the algorithm works and makes data flow in the socket outputstream
 ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 socketOutputStream ->...folderCount(Int) newFolderName(UTF) nameOfFileInFolder + "Directory" (if file is a directory) |bytesOfFile(Stream of byte array) lengthOfFile(long) nameOfFileInFolder(UTF)| folderCount(Int)  initialFolderName(UTF) -> socketInputStream
 -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

TransferFile function Pseudocode
1. Write the name of the folder to the socketDataOutputStream
2. Write the number of files in the folder to the socketDataOutputStream
3. For each file in the folder
3b. Check if the file is a directory
3c. if 3b is true, append the string "Directory" to the name of the file
3ci. Then call the transferFile function, passing it the file from 3b i.e GO TO step 1. (Recursion)
3d. if 3b is false, then
3di. Write the name of the file to the socketDataOutputStream
3dii. Write the length of the file to the socketDataOutputStream
3diii. Write the bytes of the file to the socketDataOutputStream
3div. GO TO step 3


ReceiveFile function Pseudocode
1. Read the name of the folder from the socketDataInputStream as newBaseFolderName
2. Create a new directory inside the previous baseFolder with the newBaseFolderName
2. Read the number of the files contained in the newBaseFolder from the socketDataInputStream as numberOfFilesInBaseFolder
3. for i = 0 -> numberOfFilesInBaseFolder
3a. Read the name of the file that should be inside the newBaseFolder from the socketDataInputStream
3b. if fileName ends with "Directory", then
3bi. Call the receiveFile function again passing it the baseFolder created in step 2
3c else if fileName doesn't end with "Directory", then
3ci. Read the length of the file from the socketDataInputStream
3cii. Create a new file in the base folder using the fileName read at 3a
3ciii. Read out the file content as bytes from the socketDataInputStream
3civ. Write the read out content into the file created in 3ciii
3cv. Close the fileInputStream
3cvi. GO TO step 3 until i == numberOfFilesInBaseFolder - 1
 */

class StableFileTransferProtocol(socket: Socket) {

    private val socketDOS = DataOutputStream(BufferedOutputStream(socket.getOutputStream()))
    private val socketDIS = DataInputStream(BufferedInputStream(socket.getInputStream()))

    fun transferFile(baseFolder: File) {
        socketDOS.writeUTF(baseFolder.name)
        val filesInFolder = baseFolder.listFiles()

        socketDOS.writeInt(filesInFolder.size)

        filesInFolder.forEach { file ->
            if (file.isDirectory) {
                socketDOS.writeUTF(file.name + "Directory")
                transferFile(file)
            } else {

                socketDOS.writeUTF(file.name)
                socketDOS.writeLong(file.length())

                val fileIS = FileInputStream(file)
                val bufferArray = ByteArray(5_000_000)
                var lengthRead: Int

                while (fileIS.read(bufferArray).also { lengthRead = it } > 0) {
                    socketDOS.write(bufferArray, 0, lengthRead)
                }
                fileIS.close()
            }
        }
    }

    fun receiveFile(baseFolder: File) {
        val newBaseFolderName = socketDIS.readUTF()
        val newBaseFolder = File(baseFolder, newBaseFolderName)
        newBaseFolder.mkdirs()

        val numberOfFilesInBaseFolder = socketDIS.readInt()

        for (i in 0 until numberOfFilesInBaseFolder) {
            val fileName = socketDIS.readUTF()
            if (fileName.endsWith("Directory")) {
                receiveFile(newBaseFolder)
            } else {
                var fileLength = socketDIS.readLong()

                val fileToSave = File(newBaseFolder, fileName)
                val fileOutputStream = FileOutputStream(fileToSave)
                val bufferArray = ByteArray(5_000_000)

                while (fileLength > 0) {
                    val bytesRead = socketDIS.read(bufferArray, 0, min(fileLength.toInt(), bufferArray.size))
                    if (bytesRead == -1) break
                    fileOutputStream.write(bufferArray, 0, bytesRead)
                    fileLength -= bytesRead
                }
                fileOutputStream.flush()
                fileOutputStream.close()
            }
        }
    }
}