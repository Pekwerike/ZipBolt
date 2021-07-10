package com.salesground.zipbolt.repository

import android.content.Context
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import com.bumptech.glide.Glide
import com.salesground.zipbolt.communication.MediaTransferProtocol
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.repository.implementation.ZipBoltAudioRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.*
import kotlin.math.min

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
            val audioToInsertIntoMediaStore = zipBoltAudioRepository.getAudioOnDevice().first() as DataToTransfer.DeviceAudio
            // write the audio file into the gatewat file
            context.contentResolver.openInputStream(audioToInsertIntoMediaStore.dataUri)?.let{
                val fileDataInputStream = DataInputStream(BufferedInputStream(it))
                val buffer = ByteArray(1024 * 1024)
                var dataSize = audioToInsertIntoMediaStore.dataSize

                while(dataSize > 0){
                    gateWayOutputStream.writeInt(MediaTransferProtocol.MediaTransferProtocolMetaData.KEEP_RECEIVING.value)

                    val currentReadSize = min(buffer.size.toLong(), dataSize).toInt()
                    fileDataInputStream.readFully(buffer, 0, currentReadSize)
                    gateWayOutputStream.write(buffer, 0, currentReadSize)
                    dataSize -= currentReadSize

                }
            }
            zipBoltAudioRepository.insertAudioIntoMediaStore(
                audioName = audioToInsertIntoMediaStore.dataDisplayName,
                audioSize = audioToInsertIntoMediaStore.dataSize,
                dataInputStream = gateWayInputStream,
                transferMetaDataUpdateListener = object: MediaTransferProtocol.TransferMetaDataUpdateListener{
                    override fun onMetaTransferDataUpdate(mediaTransferProtocolMetaData: MediaTransferProtocol.MediaTransferProtocolMetaData) {

                    }
                },
                dataReceiveListener = object: MediaTransferProtocol.DataReceiveListener{
                    override fun onReceive(
                        dataDisplayName: String,
                        dataSize: Long,
                        percentageOfDataRead: Float,
                        dataType: Int,
                        dataUri: Uri?,
                        dataTransferStatus: DataToTransfer.TransferStatus
                    ) {
                        assertEquals(DataToTransfer.MediaType.AUDIO.value, dataType)
                        if(dataTransferStatus == DataToTransfer.TransferStatus.RECEIVE_COMPLETE){
                            assertEquals(100f, percentageOfDataRead)
                            assert(dataUri != null)
                        } else if (dataTransferStatus == DataToTransfer.TransferStatus.RECEIVE_ONGOING ||
                            dataTransferStatus == DataToTransfer.TransferStatus.RECEIVE_STARTED
                        ) {
                            assertEquals(null, dataUri)
                        }
                    }

                },
                audioDuration = audioToInsertIntoMediaStore.audioDuration
            )
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