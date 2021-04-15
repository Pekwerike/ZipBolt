package com.salesground.zipbolt.repository

import android.content.Context
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.test.core.app.ApplicationProvider
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
import java.io.DataInputStream
import java.io.FileInputStream

@HiltAndroidTest
class ZipBoltImageRepositoryTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var zipBoltImageRepository: ZipBoltImageRepository

    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun test_getImagesOnDevice() = runBlocking {
        val allImagesOnDevice = zipBoltImageRepository.getImagesOnDevice()
        assert(allImagesOnDevice.isNotEmpty())
        val deviceImage = allImagesOnDevice[1] as DataToTransfer.DeviceImage
        assert(deviceImage.imageBucketName.isNotBlank())
        val firstTenImagesOnDevice = zipBoltImageRepository.getImagesOnDevice(limit = 10)
        assert(firstTenImagesOnDevice.size <= 10)
    }

    @Test
    fun test_getMetaDataOfImage() = runBlocking {
        var firstImage = zipBoltImageRepository.getImagesOnDevice().first()
        firstImage =
            zipBoltImageRepository.getMetaDataOfImage(firstImage) as DataToTransfer.DeviceImage
        assert(firstImage.imageMimeType.contains("image"))
        assert(firstImage.imageSize > 10)
        assert(firstImage.imageDisplayName != "")
    }

    @Test
    fun test_insertImageIntoMediaStore() {
        runBlocking {
            var numberOfImagesOnDevice = zipBoltImageRepository.getImagesOnDevice().size
            var firstImage =
                zipBoltImageRepository.getImagesOnDevice().first() as DataToTransfer.DeviceImage
            context.contentResolver.openFileDescriptor(firstImage.imageUri, "r")
                ?.also { parcelFileDescriptor: ParcelFileDescriptor ->
                    val fileInputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
                    firstImage =
                        zipBoltImageRepository.getMetaDataOfImage(firstImage) as DataToTransfer.DeviceImage
                    zipBoltImageRepository.setImageByteReadListener(
                        object : ZipBoltImageRepository.ImageByteReadListener {
                            override fun percentageOfBytesRead(bytesReadPercent: Float) {
                                assert(true)
                            }

                        }
                    )
                    zipBoltImageRepository.insertImageIntoMediaStore(
                        displayName = "firstImage.jpg",
                        size = firstImage.imageSize,
                        mimeType = firstImage.imageMimeType,
                        dataInputStream = DataInputStream(fileInputStream)
                    )
                }
            assertEquals(numberOfImagesOnDevice + 1, zipBoltImageRepository.getImagesOnDevice().size)
        }
    }
}