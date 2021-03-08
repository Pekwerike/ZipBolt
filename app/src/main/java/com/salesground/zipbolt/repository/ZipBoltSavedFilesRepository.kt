package com.salesground.zipbolt.repository
/*
* Overview
*  This class is responsible for generating the base directories for other repositories like
* ImageRepository, VideoRepository etc to saved the files of received items before inserting
* them into the media store of the various category
*
* Functions description
* 1). getZipBoltBaseDirectory() returns the top level ZipBolt directory in the file system
* 2). getZipBoltMediaCategoryBaseDirectory() returns the mediaType directory under the main directory
* 3). checkIfDirectory() exists confirm that a given directory exists, if not then it creates it
* */
import android.content.Context
import android.os.Environment
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

private const val ZIP_BOLT_MAIN_DIRECTORY = "ZipBolt"

enum class ZipBoltMediaCategory(categoryName: String){
    ZIP_BOLT_IMAGES_BASE_DIRECTORY("Images"),
    ZIP_BOLT_VIDEOS_BASE_DIRECTORY("Videos"),
    ZIP_BOLT_AUDIO_BASE_DIRECTORY("Audios"),
    ZIP_BOLT_FILES_BASE_DIRECTORY("Files"),
    ZIP_BOLT_APPS_BASE_DIRECTORY("Apps")
}

class ZipBoltSavedFilesRepository @Inject constructor() {

    private fun getZipBoltBaseDirectory(): File {
        val baseDirectory = File(Environment.getExternalStorageDirectory(), ZIP_BOLT_MAIN_DIRECTORY)
        checkIfDirectoryExist(baseDirectory)
        return baseDirectory
    }

    fun getZipBoltMediaCategoryBaseDirectory(categoryType : ZipBoltMediaCategory) : File{
        val categoryBaseDirectory = File(getZipBoltBaseDirectory(), categoryType.name)
        checkIfDirectoryExist(categoryBaseDirectory)
        return categoryBaseDirectory
    }

    private fun checkIfDirectoryExist(directory: File) {
        if (!directory.exists()) directory.mkdirs()
    }
}
