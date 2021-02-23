package com.salesground.speedforce.communicationprotocol

import java.io.File
import java.net.Socket

class FoldersFTP(private val socket: Socket) {
    private var filesCount = 0
    private val filesDTO : MutableList<FileDTO> = mutableListOf()
    fun transferFolder(folder : File){

    }

    fun folderCount(file : File){
        filesCount += 1

        if(file.isDirectory){
            val filesInDirectory = file.listFiles()

        }

    }
}