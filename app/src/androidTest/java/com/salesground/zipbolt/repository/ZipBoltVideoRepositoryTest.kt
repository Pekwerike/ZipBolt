package com.salesground.zipbolt.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import com.salesground.zipbolt.communication.MediaTransferProtocol
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.repository.implementation.ZipBoltVideoRepository
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before

import org.junit.Test
import java.io.DataInputStream
import java.io.FileInputStream

@HiltAndroidTest
class ZipBoltVideoRepositoryTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private lateinit var zipBoltVideoRepository: ZipBoltVideoRepository

    @Before
    fun setUp() {
        zipBoltVideoRepository = ZipBoltVideoRepository(
            ZipBoltSavedFilesRepository()
        )
    }


    @Test
    fun insertVideoIntoMediaStore() {
        runBlocking {
            val videoToInsertIntoMediaStore =
                zipBoltVideoRepository.getVideosOnDevice(context).first()


            zipBoltVideoRepository.insertVideoIntoMediaStore(
                context = context,
                videoName = videoToInsertIntoMediaStore.dataDisplayName,
                videoSize = videoToInsertIntoMediaStore.dataSize,
                dataInputStream = DataInputStream(
                    context.contentResolver.openInputStream(
                        videoToInsertIntoMediaStore.dataUri
                    )
                ),
                transferMetaDataUpdateListener = object :
                    MediaTransferProtocol.TransferMetaDataUpdateListener {
                    override fun onMetaTransferDataUpdate(mediaTransferProtocolMetaData: MediaTransferProtocol.MediaTransferProtocolMetaData) {

                    }
                },
                dataReceiveListener = object : MediaTransferProtocol.DataReceiveListener {
                    override fun onReceive(
                        dataDisplayName: String,
                        dataSize: Long,
                        percentageOfDataRead: Float,
                        dataType: Int,
                        dataUri: Uri?,
                        dataTransferStatus: DataToTransfer.TransferStatus
                    ) {
                        assertEquals(DataToTransfer.MediaType.VIDEO.value, dataType)
                        if (dataTransferStatus == DataToTransfer.TransferStatus.RECEIVE_COMPLETE) {
                            assertEquals(100f, percentageOfDataRead)
                            assert(dataUri != null)
                        }else if(dataTransferStatus == DataToTransfer.TransferStatus.RECEIVE_ONGOING ||
                                dataTransferStatus == DataToTransfer.TransferStatus.RECEIVE_STARTED){
                            assertEquals(null, dataUri)
                        }
                    }
                }
            )
        }
    }

    @Test
    fun getVideosOnDevice() {
        runBlocking {
            zipBoltVideoRepository.getVideosOnDevice(
                context
            ).let {
                assert(it.isNotEmpty())
                it.forEach {
                    Log.i("VideosReceived", it.dataDisplayName)
                }
            }
        }
    }
}