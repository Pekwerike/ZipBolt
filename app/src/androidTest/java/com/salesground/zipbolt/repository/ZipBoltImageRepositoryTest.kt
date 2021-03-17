package com.salesground.zipbolt.repository

import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.repository.implementation.ZipBoltImageRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import kotlinx.coroutines.*

@HiltAndroidTest
class ZipBoltImageRepositoryTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject lateinit var zipBoltImageRepository : ZipBoltImageRepository

    @Before
    fun setUp(){
        hiltRule.inject()
    }

    @Test
    fun test_getImagesOnDevice() = runBlocking {
        val allImagesOnDevice = zipBoltImageRepository.getImagesOnDevice()
        assert(allImagesOnDevice.isNotEmpty())
        val deviceImage = allImagesOnDevice[1] as DataToTransfer.DeviceImage
        assert(deviceImage.imageBucketName.isNotBlank())
        val firstTenImagesOnDevice = zipBoltImageRepository.getImagesOnDevice(limit = 10)
        assertEquals(10, firstTenImagesOnDevice.size)
    }

    @Test
    fun test_getMetaDataOfImage() = runBlocking{
        var firstImage = zipBoltImageRepository.getImagesOnDevice().first()
        firstImage = zipBoltImageRepository.getMetaDataOfImage(firstImage) as DataToTransfer.DeviceImage
        assert(firstImage.imageMimeType.contains("image"))
        assert(firstImage.imageSize > 10)
        assert(firstImage.imageDisplayName != "")
    }
}