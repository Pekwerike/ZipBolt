package com.salesground.speedforce.communicationprotocol

import java.io.File
import java.net.Socket

class FoldersFTP(private val socket: Socket) {
    private var filesCount = 0
    private val filesDTO : MutableList<FileDTO> = mutableListOf()

    fun transferFolder(folder : File){
        val socketOS = socket.getOutputStream()
        folderCount(folder)

    }

    fun folderCount(file : File){
        filesCount += 1

        if(file.isDirectory){
            val filesInDirectory = file.listFiles()!!
            filesDTO.add(FileDTO(name = file.name, childCount = filesInDirectory.size.toLong(), length = 0, file = null ))
            filesInDirectory.forEach{
                folderCount(it)
            }
        }else {
            filesDTO.add(FileDTO(name = file.name, childCount =  0, length = file.length(), file = file))
        }
    }
}