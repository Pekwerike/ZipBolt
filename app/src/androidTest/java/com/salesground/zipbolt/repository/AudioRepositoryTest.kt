package com.salesground.zipbolt.repository

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.salesground.zipbolt.utils.formatVideoDurationToString
import junit.framework.Assert
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

}