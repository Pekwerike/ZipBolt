package com.salesground.zipbolt.repository.implementation

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.bumptech.glide.Glide
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.repository.ZipBoltSavedFilesRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Test

class ZipBoltAudioRepositoryTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private lateinit var zipBoltAudioRepository: ZipBoltAudioRepository

    @Before
    fun setUp() {
        zipBoltAudioRepository = ZipBoltAudioRepository(
            savedFilesRepository = ZipBoltSavedFilesRepository(),
            context = context
        )
    }

    @After
    fun tearDown() {
    }

    @Test
    fun insertAudioIntoMediaStore() {
    }

    @Test
    fun getAudioOnDevice() {
    }

    @Test
    fun checkThat_audioAlbumUriHoldsAnImage() {
        runBlocking {
            val audioFile =
                zipBoltAudioRepository.getAudioOnDevice()[0] as DataToTransfer.DeviceAudio
            assert(Glide.with(context).load(audioFile.audioUri).submit().get() != null)
        }
    }
}