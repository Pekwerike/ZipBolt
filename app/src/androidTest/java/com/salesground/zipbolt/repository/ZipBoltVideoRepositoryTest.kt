package com.salesground.zipbolt.repository

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.salesground.zipbolt.repository.implementation.ZipBoltVideoRepository
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before

import org.junit.Test

@HiltAndroidTest
class ZipBoltVideoRepositoryTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private lateinit var zipBoltVideoRepository : ZipBoltVideoRepository

    @Before
    fun setUp(){
        zipBoltVideoRepository = ZipBoltVideoRepository(
            ZipBoltSavedFilesRepository()
        )
    }


    @Test
    fun insertVideoIntoMediaStore() {
    }

    @Test
    fun getVideosOnDevice() {
        runBlocking {
            zipBoltVideoRepository.getVideosOnDevice()
        }
    }
}