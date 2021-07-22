package com.salesground.zipbolt.repository.implementation

import android.os.Environment
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.repository.FileRepository
import java.io.File
import javax.inject.Inject

class ZipBoltFileRepository @Inject constructor() : FileRepository {

    override suspend fun getRootDirectory(): File {
        return Environment.getExternalStorageDirectory()
    }

    override suspend fun insertDirectory() {

    }

    override suspend fun insertFile() {

    }

    override suspend fun getDirectoryChildren(directoryPath: String): List<DataToTransfer> {
        var children = listOf<DataToTransfer>()
        File(directoryPath).apply {
            children = listFiles()?.map {
                DataToTransfer.DeviceFile(it)
            } ?: listOf()
        }
        return children
    }
}