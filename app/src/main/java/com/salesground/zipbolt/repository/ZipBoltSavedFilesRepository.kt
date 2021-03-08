package com.salesground.zipbolt.repository

import android.content.Context
import android.os.Environment
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

const val ZIP_BOLT_MAIN_DIRECTORY = "ZipBolt"
const val ZIP_BOLT_IMAGES_BASE_DIRECTORY = "Images"
class ZipBoltSavedFilesRepository @Inject constructor(@ApplicationContext private val
applicationContext: Context) {

    fun getZipBoltBaseDirectory() : File{
        val baseDirectory = File(Environment.getExternalStorageDirectory(), ZIP_BOLT_MAIN_DIRECTORY)
        if(!baseDirectory.exists()) baseDirectory.mkdirs()
        return baseDirectory
    }

    fun getZipBoltImagesBaseDirectory() : File {
        return File(getZipBoltBaseDirectory(), ZIP_BOLT_IMAGES_BASE_DIRECTORY)
    }
}
