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

    override fun readFileMetaData(dataInputStream: DataInputStream): Triple<String, Long, String> {
        // read the file name length
        val nameLength = dataInputStream.readInt()
        // read the file name
        val fileName = StringBuilder()
        for (i in 0 until nameLength) {
            fileName.append(dataInputStream.readChar())
        }
        // read the file size
        val fileSize = dataInputStream.readLong()
        // read the file type length
        val typeLength = dataInputStream.readInt()
        val fileMimeType = StringBuilder()
        for (i in 0 until typeLength) {
            fileMimeType.append(dataInputStream.readChar())
        }
        return Triple(fileName.toString(), fileSize, fileMimeType.toString())
    }
}