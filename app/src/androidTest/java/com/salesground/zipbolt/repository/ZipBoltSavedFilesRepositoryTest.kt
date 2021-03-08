package com.salesground.zipbolt.repository

import org.junit.Before

import org.junit.Assert.*
import org.junit.Test

class ZipBoltSavedFilesRepositoryTest {
    lateinit var zipBoltSavedFilesRepository: ZipBoltSavedFilesRepository
    @Before
    fun setUp() {
        zipBoltSavedFilesRepository = ZipBoltSavedFilesRepository()
    }

    @Test
    fun confirmThatTheBaseDirectoryNameIsZipBolt(){
        zipBoltSavedFilesRepository.getZipBoltMediaCategoryBaseDirectory(ZipBoltMediaCategory.IMAGES_BASE_DIRECTORY)
    }
}