package com.salesground.zipbolt.repository

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Before

import org.junit.Assert.*

class AudioRepositoryTest {

    private val applicationContext = ApplicationProvider.getApplicationContext<Context>()
    private lateinit var audioRepository: AudioRepository

    @Before
    fun setUp() {
        audioRepository = AudioRepository(applicationContext)
    }
}