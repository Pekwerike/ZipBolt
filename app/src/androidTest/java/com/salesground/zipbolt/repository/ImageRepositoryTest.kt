package com.salesground.zipbolt.repository

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.salesground.zipbolt.model.MediaModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import java.io.DataInputStream
import java.io.FileInputStream

@RunWith(AndroidJUnit4::class)
class ImageRepositoryTest {

    private val applicationContext = ApplicationProvider.getApplicationContext<Context>()
    lateinit var imageRepository: ImageRepository

    @Before
    fun setUp() {
        imageRepository = ImageRepository(applicationContext = applicationContext)
    }

    @Test
    fun fetchAllImage_TestCollectionIsNotEmpty() = runBlocking {
        val deviceMedia = imageRepository.fetchAllImagesOnDevice().toList()

        assertTrue(deviceMedia.size != 0)
    }

    @Test
    fun convertImageModelToFile_Test() = runBlocking {
        val deviceMedia: MutableList<MediaModel> =
            imageRepository.fetchAllImagesOnDevice().toList().toMutableList()

        val imageFiles =
            imageRepository.convertImageModelToFile(mutableListOf(deviceMedia.get(0)))

        assertEquals(imageFiles.get(0).name, deviceMedia.get(0).mediaDisplayName)
    }

    @Test
    fun convertImageModelToFile_TestForFileSize() = runBlocking {
        val deviceMedia: MutableList<MediaModel> =
            imageRepository.fetchAllImagesOnDevice().toList().toMutableList()
        val imageFiles = imageRepository.convertImageModelToFile(mutableListOf(deviceMedia.get(0)))
        assertEquals(imageFiles.get(0).length(), deviceMedia.get(0).mediaSize)
    }


    @Test
    fun insertImageIntoMediaStoreTest() = runBlocking {
        val allImagesOnDevice = imageRepository.fetchAllImagesOnDevice().toList().toMutableList()
        val firstImage = allImagesOnDevice[3]
        applicationContext.contentResolver.openFileDescriptor(firstImage.mediaUri, "r")?.also {
            val DIS = DataInputStream(FileInputStream(it.fileDescriptor))
            imageRepository.insertImageIntoMediaStore(
                firstImage.mediaDisplayName, firstImage.mediaSize,
                firstImage.mimeType,
                DIS
            )
        }
        val zipBoltImages = imageRepository.fetchAllImagesOnDevice().filter {
            it.mediaBucketName == "ZipBoltImages"
        }.toList()
        assertTrue(zipBoltImages.isNotEmpty())
    }

    @Test
    fun searchForImageByNameInMediaStoreTest(){
        val (mediaUri, mediaDisplayName, mediaDateAdded, mediaSize, mediaCategory, mimeType, mediaBucketName, mediaDuration)
        = imageRepository.fetchAllImagesOnDeviceOnce()
            .last()
        mediaDisplayName?.let {
            assertTrue(imageRepository.searchForImageByNameInMediaStore(mediaDisplayName))
        }
    }
}