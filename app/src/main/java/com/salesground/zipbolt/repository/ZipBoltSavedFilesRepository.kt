package com.salesground.zipbolt.repository

import android.content.Context
import android.os.Environment
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

private const val ZIP_BOLT_MAIN_DIRECTORY = "ZipBolt"
private const val ZIP_BOLT_IMAGES_BASE_DIRECTORY = "Images"
private const val ZIP_BOLT_VIDEOS_BASE_DIRECTORY = "Videos"
private const val ZIP_BOLT_AUDIO_BASE_DRIECTORY = "Audios"
private const val ZIP_BOLT_FILES_BASE_DIRECTORY = "Files"
private const val  ZIP_BOLT_APPS_BASE_DIRECTORY = "Apps"
enum class ZipBoltMediaCategory(categoryName: String){
    ZIP_BOLT_IMAGES_BASE_DIRECTORY("Images"),
    ZIP_BOLT_VIDEOS_BASE_DIRECTORY("Videos"),
    ZIP_BOLT_AUDIO_BASE_DIRECTORY("Audios"),
    ZIP_BOLT_FILES_BASE_DIRECTORY("Files"),
    ZIP_BOLT_APPS_BASE_DIRECTORY("Apps")
}
class ZipBoltSavedFilesRepository @Inject constructor(
    @ApplicationContext private val
    applicationContext: Context
) {

    private fun getZipBoltBaseDirectory(): File {
        val baseDirectory = File(Environment.getExternalStorageDirectory(), ZIP_BOLT_MAIN_DIRECTORY)
        if (!baseDirectory.exists()) baseDirectory.mkdirs()
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
