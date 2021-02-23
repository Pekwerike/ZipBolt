package com.salesground.speedforce.communicationprotocol

import java.io.BufferedOutputStream
import java.io.DataOutputStream
import java.io.File
import java.net.Socket

class FoldersFTP(private val socket: Socket) {
    private var filesCount = 0
    private val filesDTO : MutableList<FileDTO> = mutableListOf()

    fun transferFolder(folder : File){
        val socketOS = socket.getOutputStream()
        val socketBOS = BufferedOutputStream(socketOS)
        val socketDOS = DataOutputStream(socketBOS)

        folderCount(folder) // get the total number of files to send and group both folders and files into a filesDTO

        socketDOS.write(filesCount) // number of files to send

        filesDTO.forEach { fileDTO ->
            socketDOS.writeUTF(fileDTO.name)
            if(fileDTO.name.endsWith("Folder")){
                socketDOS.writeLong(fileDTO.childCount)
            }else {
                socketDOS.writeLong(fileDTO.length)

                val fileInputStream
            }
        }


    }

    fun folderCount(file : File){
        // increment the files count by one, for a new file
        filesCount += 1

        if(file.isDirectory){
            val filesInDirectory = file.listFiles()!!
            // add directory to the list of filesDTO
            filesDTO.add(FileDTO(name = file.name + "Folder", childCount = filesInDirectory.size.toLong(), length = 0, file = null ))
            // for every file in the directory, count them, and add them to the list of filesDTO
            filesInDirectory.forEach{
                folderCount(it)
            }
        }else {
            // add file directory to the list of filesDTO
            filesDTO.add(FileDTO(name = file.name, childCount =  0, length = file.length(), file = file))
        }
    }
}