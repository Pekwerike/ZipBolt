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
class ImageRepositoryInitialTest {

    private val applicationContext = ApplicationProvider.getApplicationContext<Context>()
    lateinit var mImageRepositoryInitial: ImageRepositoryInitial

    @Before
    fun setUp() {
        mImageRepositoryInitial = ImageRepositoryInitial(applicationContext = applicationContext,
        ZipBoltSavedFilesRepository())
    }

    @Test
    fun fetchAllImage_TestCollectionIsNotEmpty() = runBlocking {
        val deviceMedia = mImageRepositoryInitial.fetchAllImagesOnDeviceOnce()
        assertTrue(deviceMedia.size != 0)
    }

    @Test
    fun convertImageModelToFile_Test() = runBlocking {
        val deviceMedia: MutableList<MediaModel> =
            mImageRepositoryInitial.fetchAllImagesOnDeviceOnce()

        val imageFiles =
            mImageRepositoryInitial.convertImageModelToFile(mutableListOf(deviceMedia[0]))

        assertEquals(imageFiles[0].name, deviceMedia[0].mediaDisplayName)
    }

    @Test
    fun convertImageModelToFile_TestForFileSize() = runBlocking {
        val deviceMedia: MutableList<MediaModel> =
            mImageRepositoryInitial.fetchAllImagesOnDeviceOnce()
        val imageFiles = mImageRepositoryInitial.convertImageModelToFile(mutableListOf(deviceMedia.get(0)))
        assertEquals(imageFiles.get(0).length(), deviceMedia.get(0).mediaSize)
    }


    @Test
    fun insertImageIntoMediaStoreTest() = runBlocking {
        val allImagesOnDevice = mImageRepositoryInitial.fetchAllImagesOnDeviceOnce()
        val (mediaUri, mediaDisplayName, mediaDateAdded, mediaSize, mediaCategory, mimeType, mediaBucketName, mediaDuration) = allImagesOnDevice[2]

        applicationContext.contentResolver.openFileDescriptor(mediaUri, "r")?.also {
            val DIS = DataInputStream(FileInputStream(it.fileDescriptor))
            mImageRepositoryInitial.insertImageIntoMediaStore(
                allImagesOnDevice[2].mediaDisplayName, mediaSize,
                mimeType,
                DIS
            )
        }
        val zipBoltImages = mImageRepositoryInitial.fetchAllImagesOnDeviceOnce().filter {
            it.mediaBucketName == ZipBoltMediaCategory.IMAGES_BASE_DIRECTORY.categoryName
        }.toList()
        assertTrue(zipBoltImages.isNotEmpty())
    }

    @Test
    fun insertImageWithTheSameNameTwice() {
        val allImagesOnDevice = mImageRepositoryInitial.fetchAllImagesOnDeviceOnce()
        val (mediaUri, mediaDisplayName, mediaDateAdded, mediaSize, mediaCategory, mimeType, mediaBucketName, mediaDuration) = allImagesOnDevice[2]

        for(i in 0 until 3) {
            applicationContext.contentResolver.openFileDescriptor(mediaUri, "r")?.also {
                val nameUsedToSaveImage = mImageRepositoryInitial.insertImageIntoMediaStore(
                    mediaDisplayName, mediaSize,
                    mimeType,
                    DataInputStream(FileInputStream(it.fileDescriptor))
                )
           //   (mImageRepositoryInitial.searchForImageByNameInMediaStore(nameUsedToSaveImage))
            }
        }
    }
}