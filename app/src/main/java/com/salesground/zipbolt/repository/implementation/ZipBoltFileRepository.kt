package com.salesground.zipbolt.repository.implementation

import android.content.Context
import android.os.Environment
import com.salesground.zipbolt.communication.MediaTransferProtocol
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.model.MediaType
import com.salesground.zipbolt.repository.FileRepository
import com.salesground.zipbolt.service.DataTransferService
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.BufferedOutputStream
import java.io.DataInputStream
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import kotlin.math.min

class ZipBoltFileRepository @Inject constructor(
    @ApplicationContext private val context: Context
) : FileRepository {
    private val dataBuffer = ByteArray(DataTransferService.BUFFER_SIZE)
    private var sizeOfDataReadFromDirectoryTransfer: Long = 0L
    override suspend fun getRootDirectory(): File {
        return Environment.getExternalStorageDirectory()
    }

    override suspend fun insertFile() {

    }

    override suspend fun getDirectoryChildren(directoryPath: String): List<DataToTransfer> {
        var children = listOf<DataToTransfer>()
        File(directoryPath).apply {
            children = listFiles()?.map {
                DataToTransfer.DeviceFile(it).apply {
                    if (dataType == MediaType.File.Document.UnknownDocument.value) {
                        dataType = getFileType(context)
                    }
                }
            } ?: listOf()
        }
        return children
    }
}