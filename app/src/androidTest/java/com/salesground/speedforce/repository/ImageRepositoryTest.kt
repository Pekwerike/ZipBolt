package com.salesground.speedforce.repository

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.salesground.speedforce.model.ImageModel
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import java.text.SimpleDateFormat

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
        val deviceImages: MutableList<ImageModel> = imageRepository.fetchAllImagesOnDevice()

        assertTrue(deviceImages.size != 0)
    }

    @Test
    fun checkThatImageFormatIsAppendedOnImageName(){
        val deviceImages : MutableList<ImageModel> = imageRepository.fetchAllImagesOnDevice()
        deviceImages.forEach {imageModel: ImageModel ->
            val typeFormat = imageModel.imageDisplayName.takeLast(3).equals("png") || imageModel.imageDisplayName.takeLast(3).equals("jpg")
            assertTrue(typeFormat)
        }
    }
    @Test
    fun convertImageModelToFile_Test() {
        val deviceImages: MutableList<ImageModel> = imageRepository.fetchAllImagesOnDevice()

        val imageFiles =
            imageRepository.convertImageModelToFile(mutableListOf(deviceImages.get(0)))

        assertEquals(imageFiles.get(0).name, deviceImages.get(0).imageDisplayName)
    }

    @Test
    fun convertImageModelToFile_TestForFileSize(){
        val deviceImages : MutableList<ImageModel> = imageRepository.fetchAllImagesOnDevice()
        val imageFiles = imageRepository.convertImageModelToFile(mutableListOf(deviceImages.get(0)))
        assertEquals(imageFiles.get(0).length(), deviceImages.get(0).imageSize)
    }

}