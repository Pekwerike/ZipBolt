package com.salesground.zipbolt.repository

import android.content.Context
import android.os.Environment
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

const val ZIP_BOLT_MAIN_DIRECTORY = "ZipBolt"
class ZipBoltSavedFilesRepository @Inject constructor(@ApplicationContext private val
applicationContext: Context) {

    fun getZipBoltBaseDirectory() : File{
      return File(Environment.getExternalStorageDirectory(), ZIP_BOLT_MAIN_DIRECTORY)
    }

    fun getZipBoltImagesBaseDirectory()
}
