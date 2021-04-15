package com.salesground.zipbolt.repository

import java.io.File

interface SavedFilesRepository {
    fun getZipBoltBaseDirectory(): File
    fun getZipBoltMediaCategoryBaseDirectory(categoryType: ZipBoltMediaCategory): File
    fun checkIfDirectoryExist(directory: File)
}