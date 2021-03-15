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
        imageRepository = ImageRepository(applicationContext = applicationContext,
        ZipBoltSavedFilesRepository())
    }

    @Test
    fun fetchAllImage_TestCollectionIsNotEmpty() = runBlocking {
        val deviceMedia = imageRepository.fetchAllImagesOnDeviceOnce()
        assertTrue(deviceMedia.size != 0)
    }

    @Test
    fun convertImageModelToFile_Test() = runBlocking {
        val deviceMedia: MutableList<MediaModel> =
            imageRepository.fetchAllImagesOnDeviceOnce()

        val imageFiles =
            imageRepository.convertImageModelToFile(mutableListOf(deviceMedia[0]))

        assertEquals(imageFiles[0].name, deviceMedia[0].mediaDisplayName)
    }

    @Test
    fun convertImageModelToFile_TestForFileSize() = runBlocking {
        val deviceMedia: MutableList<MediaModel> =
            imageRepository.fetchAllImagesOnDeviceOnce()
        val imageFiles = imageRepository.convertImageModelToFile(mutableListOf(deviceMedia.get(0)))
        assertEquals(imageFiles.get(0).length(), deviceMedia.get(0).mediaSize)
    }


    @Test
    fun insertImageIntoMediaStoreTest() = runBlocking {
        val allImagesOnDevice = imageRepository.fetchAllImagesOnDeviceOnce()
        val (mediaUri, mediaDisplayName, mediaDateAdded, mediaSize, mediaCategory, mimeType, mediaBucketName, mediaDuration) = allImagesOnDevice[2]

        applicationContext.contentResolver.openFileDescriptor(mediaUri, "r")?.also {
            val DIS = DataInputStream(FileInputStream(it.fileDescriptor))
            imageRepository.insertImageIntoMediaStore(
                allImagesOnDevice[2].mediaDisplayName, mediaSize,
                mimeType,
                DIS
            )
        }
        val zipBoltImages = imageRepository.fetchAllImagesOnDeviceOnce().filter {
            it.mediaBucketName == ZipBoltMediaCategory.IMAGES_BASE_DIRECTORY.categoryName
        }.toList()
        assertTrue(zipBoltImages.isNotEmpty())
    }

    @Test
    fun insertImageWithTheSameNameTwice() {
        val allImagesOnDevice = imageRepository.fetchAllImagesOnDeviceOnce()
        val (mediaUri, mediaDisplayName, mediaDateAdded, mediaSize, mediaCategory, mimeType, mediaBucketName, mediaDuration) = allImagesOnDevice[2]

        for(i in 0 until 3) {
            applicationContext.contentResolver.openFileDescriptor(mediaUri, "r")?.also {
                val nameUsedToSaveImage = imageRepository.insertImageIntoMediaStore(
                    mediaDisplayName, mediaSize,
                    mimeType,
                    DataInputStream(FileInputStream(it.fileDescriptor))
                )
                assertTrue(imageRepository.searchForImageByNameInMediaStore(nameUsedToSaveImage))
            }
        }
    }
}