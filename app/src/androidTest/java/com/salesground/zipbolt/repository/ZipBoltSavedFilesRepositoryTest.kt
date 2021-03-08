package com.salesground.zipbolt.repository

import org.junit.Before

import org.junit.Assert.*
import org.junit.Test
import java.util.zip.ZipInputStream

class ZipBoltSavedFilesRepositoryTest {
    lateinit var zipBoltSavedFilesRepository: ZipBoltSavedFilesRepository

    @Before
    fun setUp() {
        zipBoltSavedFilesRepository = ZipBoltSavedFilesRepository()
    }

    @Test
    fun confirmThatTheAppRootDirectoryNameIsZipBolt() {
        val imageBaseDirectory = zipBoltSavedFilesRepository
            .getZipBoltMediaCategoryBaseDirectory(ZipBoltMediaCategory.IMAGES_BASE_DIRECTORY)
        assertEquals(imageBaseDirectory.parentFile.name, "ZipBolt")
    }

    @Test
    fun confirmThatDirectoriesAreCreatedForEachMediaCategory() {
        val mediaCategory = ZipBoltMediaCategory.values()
        mediaCategory.forEach {
            val mediaCategory =
                zipBoltSavedFilesRepository.getZipBoltMediaCategoryBaseDirectory(it)
            assertTrue(mediaCategory.exists())
        }
    }

    @Test
    fun confirmThatDirectoryForImagesHasTheNameImages(){
        val imageCategory = ZipBoltMediaCategory.IMAGES_BASE_DIRECTORY
        val imageCategoryBaseDirectory =
            zipBoltSavedFilesRepository.getZipBoltMediaCategoryBaseDirectory(imageCategory)
        assertEquals(imageCategoryBaseDirectory.name, "Images")
    }

}