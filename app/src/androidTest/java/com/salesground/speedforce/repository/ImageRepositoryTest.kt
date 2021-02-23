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
    lateinit var imageRepository : ImageRepository
    @Before
    fun setUp() {
        imageRepository = ImageRepository(applicationContext = applicationContext)
    }

    @Test
    fun fetchAllImage_TestCollectionIsNotEmpty() {
        val deviceImages : MutableList<ImageModel> = imageRepository.fetchAllImagesOnDevice()

        assertTrue(deviceImages.size != 0)
    }

    @Test
    fun convertImageModelToFile_Test(){
        val deviceImages : MutableList<ImageModel> = imageRepository.fetchAllImagesOnDevice()
    }

}