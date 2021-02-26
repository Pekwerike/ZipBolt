package com.salesground.zipbolt.repository

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.salesground.zipbolt.model.MediaModel
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ImageRepositoryTest {

    val applicationContext = ApplicationProvider.getApplicationContext<Context>()
    lateinit var imageRepository: ImageRepository

    @Before
    fun setUp() {
        imageRepository = ImageRepository(applicationContext = applicationContext)
    }

    @Test
    fun fetchAllImage_TestCollectionIsNotEmpty() {
        val deviceMedia: MutableList<MediaModel> = imageRepository.fetchAllImagesOnDevice()

        assertTrue(deviceMedia.size != 0)
    }

    @Test
    fun checkThatImageFormatIsAppendedOnImageName(){
        val deviceMedia : MutableList<MediaModel> = imageRepository.fetchAllImagesOnDevice()
        deviceMedia.forEach { mediaModel: MediaModel ->
            val typeFormat = mediaModel.mediaDisplayName.takeLast(3).equals("png") || mediaModel.mediaDisplayName.takeLast(3).equals("jpg")
            assertTrue(typeFormat)
        }
    }
    @Test
    fun convertImageModelToFile_Test() {
        val deviceMedia: MutableList<MediaModel> = imageRepository.fetchAllImagesOnDevice()

        val imageFiles =
            imageRepository.convertImageModelToFile(mutableListOf(deviceMedia.get(0)))

        assertEquals(imageFiles.get(0).name, deviceMedia.get(0).mediaDisplayName)
    }

    @Test
    fun convertImageModelToFile_TestForFileSize(){
        val deviceMedia : MutableList<MediaModel> = imageRepository.fetchAllImagesOnDevice()
        val imageFiles = imageRepository.convertImageModelToFile(mutableListOf(deviceMedia.get(0)))
        assertEquals(imageFiles.get(0).length(), deviceMedia.get(0).mediaSize)
    }

}