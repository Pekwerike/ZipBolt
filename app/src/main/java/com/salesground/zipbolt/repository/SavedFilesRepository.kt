package com.salesground.zipbolt.repository

import com.salesground.zipbolt.repository.ZipBoltMediaCategory
import java.io.File

interface SavedFilesRepository {
    fun getZipBoltBaseDirectory(): File
    fun getZipBoltMediaCategoryBaseDirectory(categoryType: ZipBoltMediaCategory): File
    fun checkIfDirectoryExist(directory: File)
}