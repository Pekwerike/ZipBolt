package com.salesground.zipbolt.repository.implementation

import android.os.Environment
import com.salesground.zipbolt.repository.FileRepository
import java.io.File

class ZipBoltFileRepository : FileRepository {

    override suspend fun getRootDirectory(): File {
        return Environment.getExternalStorageDirectory()
    }

    override suspend fun getDirectoryChildren(directoryPath: String): Array<File> {
        var childFiles = arrayOf<File>()
        File(directoryPath).let { file: File ->
            file.listFiles()?.let {
                childFiles = it
            }
        }
        return childFiles
    }

    override suspend fun insertDirectory() {

    }

    override suspend fun insertFile() {

    }

}