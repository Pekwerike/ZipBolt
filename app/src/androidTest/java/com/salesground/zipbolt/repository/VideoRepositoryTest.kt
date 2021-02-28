package com.salesground.zipbolt.repository

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.test.core.app.ApplicationProvider
import com.salesground.zipbolt.model.MediaCategory
import com.salesground.zipbolt.model.MediaModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test

class VideoRepositoryTest {

    private val applicationContext = ApplicationProvider.getApplicationContext<Context>()
    private lateinit var videoRepository: VideoRepository

    @Before
    fun setUp() {
        videoRepository = VideoRepository(applicationContext)
    }


    @Test
    fun test_getAllVideosFromDeviceAsFlow_emitsValue() = runBlocking {
        val firstVideoOnDevice: MediaModel = videoRepository.getAllVideoFromDeviceAsFlow().first()

        // check that the video name is not empty
        assertTrue(firstVideoOnDevice.mediaDisplayName.isNotEmpty())
        // check that .mp4 is appended at the end of the video name
        assertEquals(firstVideoOnDevice.mediaDisplayName.takeLast(3), "mp4")
        // check that the media type is of the category video
        assertEquals(firstVideoOnDevice.mediaCategory, MediaCategory.VIDEO)
        // check that the mime type returned by the media store contains mp4
        assertTrue(firstVideoOnDevice.mimeType.contains("video/"))
    }

    @Test
    fun test_getAllVideosFromDeviceAsFlow_collectAllValues() = runBlocking {
        val allVideosOnDeviceState: MutableState<MutableList<MediaModel>> =
            mutableStateOf(mutableListOf())
        val allVideosOnDevice: MutableList<MediaModel> = mutableListOf()
        val allFilteredVideosOnDevice: MutableList<MediaModel> = mutableListOf()

        // check that multiple videos are collected
        videoRepository.getAllVideoFromDeviceAsFlow().collect { video: MediaModel ->
            allVideosOnDevice.add(video)
            allVideosOnDeviceState.value = allVideosOnDevice
        }
        assertTrue(allVideosOnDeviceState.value.size > 1)

        // test that the filter lambda functions as expected
        videoRepository.getAllVideoFromDeviceAsFlow().filter {
            it.mediaCategory == MediaCategory.IMAGE
        }.collect {
            allFilteredVideosOnDevice.add(it)
        }

        assertEquals(allFilteredVideosOnDevice.size, 0)

    }

    /*
    This test should only pass on 2 conditions
    1. Devices that have at least on video in the shared storage
    2. All videos in the device must be caputered through the camera
    */
    @Test
    fun confirm_thatVideoRepositoryFetchsTheParentFolderOfEachVideo() = runBlocking {
        videoRepository.getAllVideoFromDeviceAsFlow().collect { mediaModel ->
            assertEquals( "Camera", mediaModel.mediaBucketName)
        }
    }

    @After
    fun tearDown() {
    }
}