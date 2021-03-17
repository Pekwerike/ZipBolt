package com.salesground.zipbolt.repository

import com.salesground.zipbolt.HiltTestRunner
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.repository.implementation.ZipBoltImageRepository
import dagger.hilt.android.testing.CustomTestApplication
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

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
    fun test_getAllImagesOnDevice() {
        val allImagesOnDevice = zipBoltImageRepository.getAllImagesOnDevice()
        assert(allImagesOnDevice.isNotEmpty())
        val deviceImage = allImagesOnDevice[1] as DataToTransfer.DeviceImage
        assert(deviceImage.imageBucketName.isNotBlank())
    }
}