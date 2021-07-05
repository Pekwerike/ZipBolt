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
import java.io.*

class ZipBoltAudioRepositoryTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private lateinit var zipBoltAudioRepository: ZipBoltAudioRepository

    // the gateWay streams will try to recreate a socket connection for stream so that,
    // we can test the insertVideoIntoMediaStore function
    private lateinit var gateWayFile: File
    private lateinit var gateWayInputStream: DataInputStream
    private lateinit var gateWayOutputStream: DataOutputStream

    @Before
    fun setUp() {
        zipBoltAudioRepository = ZipBoltAudioRepository(
            savedFilesRepository = ZipBoltSavedFilesRepository(),
            context = context
        )
        gateWayFile = File(context.getExternalFilesDir(null), "Gateway.txt")
        gateWayOutputStream = DataOutputStream(
            BufferedOutputStream(
                FileOutputStream(gateWayFile)
            )
        )
        gateWayInputStream = DataInputStream(
            BufferedInputStream(
                FileInputStream(gateWayFile)
            )
        )
    }

    @After
    fun tearDown() {
        gateWayFile.delete()
    }

    @Test
    fun insertAudioIntoMediaStore() {
        runBlocking{
            val audioToInsertIntoMediaStore = zipBoltAudioRepository.getAudioOnDevice().first()

            // write the audio file into the gatewat file
        }
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