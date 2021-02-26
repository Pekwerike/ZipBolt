package com.salesground.zipbolt.repository

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.salesground.zipbolt.model.MediaCategory
import com.salesground.zipbolt.model.MediaModel
import kotlinx.coroutines.flow.collect
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
        assertTrue(firstVideoOnDevice.mimeType.contains("mp4") )
    }

    @Test
    fun test_getAllVideosFromDeviceAsFlow_collectAllValues() = runBlocking{
        val allVideosOnDevice
        videoRepository.getAllVideoFromDeviceAsFlow().collect { video : MediaModel ->

        }
    }

    @After
    fun tearDown() {
    }
}