package com.salesground.zipbolt.repository

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.salesground.zipbolt.model.MediaModel
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import java.io.DataInputStream
import java.io.FileInputStream

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
    fun checkThatImageFormatIsAppendedOnImageName() {
        val deviceMedia: MutableList<MediaModel> = imageRepository.fetchAllImagesOnDevice()
        deviceMedia.forEach { mediaModel: MediaModel ->
            val typeFormat = mediaModel.mediaDisplayName.takeLast(3)
                .equals("png") || mediaModel.mediaDisplayName.takeLast(3).equals("jpg")
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
    fun convertImageModelToFile_TestForFileSize() {
        val deviceMedia: MutableList<MediaModel> = imageRepository.fetchAllImagesOnDevice()
        val imageFiles = imageRepository.convertImageModelToFile(mutableListOf(deviceMedia.get(0)))
        assertEquals(imageFiles.get(0).length(), deviceMedia.get(0).mediaSize)
    }

    /*
    This test should only pass on 2 conditions
    1. Devices that have at least on photo in the shared storage
    2. All photos in the device must be caputered through the camera
    */
    @Test
    fun fetchAllImagesOnDevice_returnsTheBucketNameOfAllImages() = runBlocking {
        val allImagesOnDevice = imageRepository.fetchAllImagesOnDevice()
        allImagesOnDevice.forEach {
            assertEquals(it.mediaBucketName, "Camera")
            assertTrue(it.mediaBucketName.startsWith("Camera"))
        }
    }

    @Test
    fun insertImageIntoMediaStoreTest() = runBlocking {
        val allImagesOnDevice = imageRepository.fetchAllImagesOnDevice()
        val firstImage = allImagesOnDevice.get(0)
        applicationContext.contentResolver.openFileDescriptor(firstImage.mediaUri, "r")?.also {
            val DIS = DataInputStream(FileInputStream(it.fileDescriptor))
            imageRepository.insertImageIntoMediaStore(
                firstImage.mediaDisplayName + System.currentTimeMillis(), firstImage.mediaSize,
                DIS
            )
        }
        val newCollectionOfImages = imageRepository.fetchAllImagesOnDevice()
        assertEquals(newCollectionOfImages.first().mediaBucketName, "ZipBoltImages")
    }
}