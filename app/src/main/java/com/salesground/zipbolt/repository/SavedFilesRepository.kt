package com.salesground.zipbolt.repository

import java.io.File

interface SavedFilesRepository {

    enum class ZipBoltMediaCategory(val categoryName: String) {
        IMAGES_BASE_DIRECTORY("ZipBolt Images"),
        VIDEOS_BASE_DIRECTORY("ZipBolt Videos"),
        AUDIO_BASE_DIRECTORY("ZipBolt Audios"),
        FILES_BASE_DIRECTORY("Files"),
        APPS_BASE_DIRECTORY("Apps"),
        FOLDERS_BASE_DIRECTORY("Folders"),
        DOCUMENTS_BASE_DIRECTORY("ZipBolt Documents")
    }

    fun getZipBoltBaseDirectory(): File
    fun getZipBoltMediaCategoryBaseDirectory(categoryType: ZipBoltMediaCategory): File
}