package com.salesground.zipbolt.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.salesground.zipbolt.TestCoroutineRule
import com.salesground.zipbolt.fakerepository.FakeZipBoltImageRepository
import com.salesground.zipbolt.getOrAwaitValue
import com.salesground.zipbolt.model.ui.ImagesDisplayModel
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(manifest=Config.NONE)
class ImagesViewModelTest {
    private lateinit var mImagesViewModel: ImagesViewModel

    @get:Rule
    var instantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var testCoroutineRule : TestRule = TestCoroutineRule()

    @Before
    fun setUp() {
        mImagesViewModel = ImagesViewModel(imageRepository = FakeZipBoltImageRepository())
    }

    @Test
    fun testThat_deviceImagesBucketName_isNotEmpty(){
        val imageBucketNames = mImagesViewModel.deviceImagesBucketName.getOrAwaitValue()
        assert(imageBucketNames.isNotEmpty())
        assertEquals("All", imageBucketNames.first().bucketName)
        assertEquals("Whatsapp", imageBucketNames[1].bucketName)
    }

    @Test
    fun test_filterDeviceImages() = runBlocking{
        mImagesViewModel.filterDeviceImages(bucketName = "Whatsapp")
        delay(1000) // simulate delay so that a new live data value can be observed
        val whatsAppImages = mImagesViewModel.deviceImagesGroupedByDateModified.getOrAwaitValue()
        assert(whatsAppImages.isNotEmpty())
        whatsAppImages.forEach {
            when(it){
                is ImagesDisplayModel.DeviceImageDisplay -> {
                    assertEquals("Whatsapp", it.deviceImage.imageBucketName)
                    assert(it.deviceImage.imageBucketName != "ZipBolt" && it.deviceImage.imageBucketName != "Camera")
                }
                is ImagesDisplayModel.ImagesDateModifiedHeader -> assert(it.dateModified.isNotBlank())
            }
        }
        mImagesViewModel.filterDeviceImages(bucketName = "ZipBolt")
        delay(1000) // simulate delay so that a new live data value can be observed
        val zipBoltImages = mImagesViewModel.deviceImagesGroupedByDateModified.getOrAwaitValue()
        assert(zipBoltImages.isNotEmpty())
        zipBoltImages.forEach {
            when(it){
                is ImagesDisplayModel.DeviceImageDisplay -> {
                    assertEquals("ZipBolt", it.deviceImage.imageBucketName )
                    assert(it.deviceImage.imageBucketName != "Whatsapp" && it.deviceImage.imageBucketName != "Camera")
                }
                is ImagesDisplayModel.ImagesDateModifiedHeader -> assert(it.dateModified.isNotBlank())
            }
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_deviceImagesGroupedByDateModified_liveData() {
     //   shadowOf(getMainLooper()).idle()
        val imagesOnDevice =
            mImagesViewModel.deviceImagesGroupedByDateModified.getOrAwaitValue()
        assert(!imagesOnDevice.isNullOrEmpty())
        imagesOnDevice.forEach {
            when(it){
                is ImagesDisplayModel.DeviceImageDisplay -> println(it.deviceImage.imageDisplayName)
                is ImagesDisplayModel.ImagesDateModifiedHeader -> println(it.dateModified)
            }
        }
    }
}