package com.salesground.zipbolt.communicationprotocol.implementation

import android.content.Context
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.repository.ImageRepository
import java.io.DataInputStream
import java.io.DataOutputStream
import java.lang.StringBuilder

class MediaTransferProtocolForNonJavaServers(
    context: Context,
    advancedImageRepository: ImageRepository
) : AdvanceMediaTransferProtocol(context, advancedImageRepository) {

    private val fileName = StringBuilder()
    private val fileType = StringBuilder()

    override fun writeFileMetaData(
        dataOutputStream: DataOutputStream,
        dataToTransfer: DataToTransfer
    ) {
        // write the file name length
        dataOutputStream.writeInt(dataToTransfer.dataDisplayName.length)
        // write the file name
        dataOutputStream.writeChars(dataToTransfer.dataDisplayName)
        // write the file size
        dataOutputStream.writeLong(dataToTransfer.dataSize)
        // write the file mime type length
        dataOutputStream.writeInt(dataToTransfer.dataType.length)
        // write the file type
        dataOutputStream.writeChars(dataToTransfer.dataType)
    }
    override fun readFileName(dataInputStream: DataInputStream): String {
        fileName.setLength(0)
        // read the file name length
        val nameLength = dataInputStream.readInt()
        // read the file name

        for (i in 0 until nameLength) {
            fileName.append(dataInputStream.readChar())
        }
        return fileName.toString()
    }


    override fun readFileType(dataInputStream: DataInputStream): String {
        fileType.setLength(0)
        // read the file type length
        val typeLength = dataInputStream.readInt()
        for (i in 0 until typeLength) {
            fileType.append(dataInputStream.readChar())
        }
        return fileType.toString()
    }
}