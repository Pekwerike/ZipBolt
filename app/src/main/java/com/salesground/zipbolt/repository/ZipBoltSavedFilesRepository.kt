package com.salesground.zipbolt.repository

import android.content.Context
import android.os.Environment
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

const val ZIP_BOLT_MAIN_DIRECTORY = "ZipBolt"
const val ZIP_BOLT_IMAGES_BASE_DIRECTORY = "Images"
const val ZIP_BOLT_VIDEOS_BASE_DIRECTORY = "Videos"
const val ZIP_BOLT_FILES_BASE_DIRECTORY = "Files"
const val  ZIP_BOLT_APPS_BASE_DIRECTORY = "Apps"

class ZipBoltSavedFilesRepository @Inject constructor(
    @ApplicationContext private val
    applicationContext: Context
) {

    private fun getZipBoltBaseDirectory(): File {
        val baseDirectory = File(Environment.getExternalStorageDirectory(), ZIP_BOLT_MAIN_DIRECTORY)
        if (!baseDirectory.exists()) baseDirectory.mkdirs()
        return baseDirectory
    }

    fun getZipBoltImagesBaseDirectory(): File {
        val imageBaseDirectory = File(getZipBoltBaseDirectory(), ZIP_BOLT_IMAGES_BASE_DIRECTORY)
        checkIfDirectoryExist(imageBaseDirectory)
        return imageBaseDirectory
    }

    fun getZipBoltVideosBaseDirectory() : File{
        val videoBaseDirectory = File(getZipBoltBaseDirectory(), )
    }

    private fun checkIfDirectoryExist(directory: File) {
        if (!directory.exists()) directory.mkdirs()
    }
}
