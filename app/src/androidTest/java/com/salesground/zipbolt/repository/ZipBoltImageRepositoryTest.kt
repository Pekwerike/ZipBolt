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
            zipBoltImageRepository.getMetaDataOfImage(firstImage as DataToTransfer.DeviceImage) as DataToTransfer.DeviceImage
        assert(firstImage.imageMimeType.contains("image"))
        assert(firstImage.imageSize > 10)
        assert(firstImage.imageDisplayName != "")
    }

    @Test
    fun test_insertImageIntoMediaStore() {
        runBlocking {
            var numberOfImagesOnDevice = zipBoltImageRepository.getImagesOnDevice().size
            var firstImage = zipBoltImageRepository.getMetaDataOfImage(
                zipBoltImageRepository.getImagesOnDevice(limit = 5)[0] as DataToTransfer.DeviceImage
            ) as DataToTransfer.DeviceImage

            var secondImage = zipBoltImageRepository.getMetaDataOfImage(
                zipBoltImageRepository.getImagesOnDevice(limit = 5)[1] as DataToTransfer.DeviceImage
            ) as DataToTransfer.DeviceImage

            zipBoltImageRepository.setImageByteReadListener(
                object : ImageRepository.ImageByteReadListener {
                    override fun percentageOfBytesRead(bytesReadPercent: Pair<String, Float>) {
                        Log.i("ImageTransferPercent", "${bytesReadPercent.first}: ${bytesReadPercent.second}")
                    }
                }
            )

            context.contentResolver.openFileDescriptor(firstImage.imageUri, "r")
                ?.also { parcelFileDescriptor: ParcelFileDescriptor ->
                    zipBoltImageRepository.insertImageIntoMediaStore(
                        displayName = "firstImage.jpg",
                        size = firstImage.imageSize,
                        mimeType = firstImage.imageMimeType,
                        dataInputStream = DataInputStream(FileInputStream(parcelFileDescriptor.fileDescriptor))
                    )
                }

            context.contentResolver.openFileDescriptor(secondImage.imageUri, "r")
                ?.also { parcelFileDescriptor ->
                    zipBoltImageRepository.insertImageIntoMediaStore(
                        displayName = secondImage.imageDisplayName,
                        size = secondImage.imageSize,
                        mimeType = secondImage.imageMimeType,
                        dataInputStream = DataInputStream(FileInputStream(parcelFileDescriptor.fileDescriptor))
                    )
                }
            assertEquals(
                numberOfImagesOnDevice + 2,
                zipBoltImageRepository.getImagesOnDevice().size
            )
        }
    }
}