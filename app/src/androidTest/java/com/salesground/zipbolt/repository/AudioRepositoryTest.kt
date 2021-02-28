package com.salesground.zipbolt.repository

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test

class AudioRepositoryTest {

    private val applicationContext = ApplicationProvider.getApplicationContext<Context>()
    private lateinit var audioRepository: AudioRepository

    @Before
    fun setUp() {
        audioRepository = AudioRepository(applicationContext)
    }

    @Test
    fun testThat_theDurationOfEachVideoIsReturned() = runBlocking{
        audioRepository.getAllAudioFilesOnDevice().collect {
            assertTrue(it.mediaDuration > 10)
        }
    }
}