package com.salesground.zipbolt.repository

import com.salesground.zipbolt.ZipBoltApplication
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.repository.implementation.ZipBoltImageRepository
import com.salesground.zipbolt.testrunner.HiltTestRunner
import dagger.hilt.android.testing.CustomTestApplication
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class ZipBoltImageRepositoryTest {

    @Inject lateinit var zipBoltImageRepository : ZipBoltImageRepository

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun setUp(){
        hiltRule.inject()
    }

    @Test
    fun test_getAllImagesOnDevice() = runBlocking {
        val allImagesOnDevice = zipBoltImageRepository.getAllImagesOnDevice()
        assert(allImagesOnDevice.isNotEmpty())
        val deviceImage = allImagesOnDevice[1] as DataToTransfer.DeviceImage
        assert(deviceImage.imageBucketName.isNotBlank())
    }
}