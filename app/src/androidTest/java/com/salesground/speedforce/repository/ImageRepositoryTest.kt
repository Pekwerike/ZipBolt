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
    fun testthat_FetchedImagesIs_SortedByDateIn_AscendingOrder(){
        val deviceImages : MutableList<ImageModel> = imageRepository.fetchAllImagesOnDevice()
        val simpleDateFormat = SimpleDateFormat("yyyy-MMM-dd-kk-mm-sss-S")
        val dateCreatedFirstImage = simpleDateFormat.parse(simpleDateFormat.format(deviceImages.get(0).imageDateAdded))
        val dateCreatedSecondImage =simpleDateFormat.parse(simpleDateFormat.format(deviceImages.get(1).imageDateAdded))
        val dateCreatedThirdImage = simpleDateFormat.parse(simpleDateFormat.format(deviceImages.get(2).imageDateAdded))
        when(deviceImages.size){
             in 1..3000 -> {
                 assertTrue(dateCreatedFirstImage.compareTo(dateCreatedSecondImage) < 0)
             }
            2 -> {
                assertTrue(deviceImages.get(0).imageDateAdded < deviceImages.get(1).imageDateAdded)
            }else -> {
            // device doesn't have any image
                assertTrue(true)
            }
        }

    }

}