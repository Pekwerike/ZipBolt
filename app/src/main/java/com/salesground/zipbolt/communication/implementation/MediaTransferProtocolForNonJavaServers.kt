package com.salesground.zipbolt.communication.implementation

import android.content.Context
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.repository.ImageRepository
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.DataInputStream
import java.io.DataOutputStream
import java.lang.StringBuilder
import javax.inject.Inject

class MediaTransferProtocolForNonJavaServers @Inject constructor(
    @ApplicationContext context : Context,
    private val advancedImageRepository: ImageRepository
) : AdvanceMediaTransferProtocol(context, advancedImageRepository) {

    private val fileName = StringBuilder()
    private val fileType = StringBuilder()

    override suspend fun writeFileMetaData(
        dataOutputStream: DataOutputStream,
        dataToTransfer: DataToTransfer
    ) {
        val mdataToTransfer = advancedImageRepository.getMetaDataOfImage(dataToTransfer as DataToTransfer.DeviceImage)
        // write the file name length
        dataOutputStream.writeInt(mdataToTransfer.dataDisplayName.length)
        // write the file name
        dataOutputStream.writeChars(mdataToTransfer.dataDisplayName)
        // write the file size
        dataOutputStream.writeLong(mdataToTransfer.dataSize)
        // write the file mime type length
        dataOutputStream.writeInt(mdataToTransfer.dataType.length)
        // write the file type
        dataOutputStream.writeChars(mdataToTransfer.dataType)
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