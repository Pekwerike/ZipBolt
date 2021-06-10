package com.salesground.zipbolt.repository
/*
* Overview
*  This class is responsible for generating the base directories for other repositories like
* ImageRepositoryInitial, VideoRepository etc to saved the files of received items before inserting
* them into the media store of the various category
*
* Functions description
* 1). getZipBoltBaseDirectory() returns the top level ZipBolt directory in the file system
* 2). getZipBoltMediaCategoryBaseDirectory() returns the mediaType directory under the main directory
* 3). checkIfDirectory() exists confirm that a given directory exists, if not then it creates it
* */
import android.os.Environment
import java.io.File
import javax.inject.Inject

const val ZIP_BOLT_MAIN_DIRECTORY = "ZipBolt"

class ZipBoltSavedFilesRepository @Inject constructor() : SavedFilesRepository {

    override fun getZipBoltBaseDirectory(): File {
        val baseDirectory = File(Environment.getExternalStorageDirectory(), ZIP_BOLT_MAIN_DIRECTORY)
        checkIfDirectoryExist(baseDirectory)
        return baseDirectory
    }

    override fun getZipBoltMediaCategoryBaseDirectory(categoryType: SavedFilesRepository.ZipBoltMediaCategory): File {
        val categoryBaseDirectory = File(getZipBoltBaseDirectory(), categoryType.categoryName)
        checkIfDirectoryExist(categoryBaseDirectory)
        return categoryBaseDirectory
    }

    private fun checkIfDirectoryExist(directory: File) {
        if (!directory.exists()) directory.mkdirs()
    }
}
