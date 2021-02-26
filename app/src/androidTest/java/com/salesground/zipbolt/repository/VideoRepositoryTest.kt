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

        assertTrue(firstVideoOnDevice.mediaDisplayName.isNotEmpty())
        assertEquals(firstVideoOnDevice.mediaCategory, MediaCategory.VIDEO)
        assertTrue(firstVideoOnDevice.mimeType.contains("mp4") )

    }

    @After
    fun tearDown() {
    }
}