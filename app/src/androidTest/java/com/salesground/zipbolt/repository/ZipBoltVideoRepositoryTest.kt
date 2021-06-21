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
import org.junit.After
import org.junit.Assert.*
import org.junit.Before

import org.junit.Test
import java.io.*
import kotlin.math.min

@HiltAndroidTest
class ZipBoltVideoRepositoryTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private lateinit var zipBoltVideoRepository: ZipBoltVideoRepository

    // the gateWay streams will try to recreate a socket connection for stream so that,
    // we can test the insertVideoIntoMediaStore function
    private lateinit var gateWayFile: File
    private lateinit var gateWayInputStream: DataInputStream
    private lateinit var gateWayOutputStream: DataOutputStream

    @Before
    fun setUp() {
        zipBoltVideoRepository = ZipBoltVideoRepository(
            ZipBoltSavedFilesRepository()
        )

        gateWayFile = File(context.getExternalFilesDir(null), "Gateway.txt")
        gateWayInputStream = DataInputStream(
            BufferedInputStream(
                FileInputStream(gateWayFile)
            )
        )
        gateWayOutputStream = DataOutputStream(
            BufferedOutputStream(
                FileOutputStream(gateWayFile)
            )
        )
    }

    @After
    fun destory() {
        gateWayOutputStream.close()
        gateWayInputStream.close()
        gateWayFile.delete()
    }


    @Test
    fun insertVideoIntoMediaStore() {
        runBlocking {
            val videoToInsertIntoMediaStore =
                zipBoltVideoRepository.getVideosOnDevice(context).first()

            // write the video file into the gateway file
            context.contentResolver.openInputStream(videoToInsertIntoMediaStore.dataUri)?.let {
                val fileDataInputStream = DataInputStream(BufferedInputStream(it))

                val buffer = ByteArray(1024 * 1024)
                var dataSize = videoToInsertIntoMediaStore.dataSize

                while(dataSize > 0){
                    fileDataInputStream.readFully(buffer, 0, (min(buffer.size.toLong(), dataSize).toInt()))

                }
            }


            zipBoltVideoRepository.insertVideoIntoMediaStore(
                context = context,
                videoName = videoToInsertIntoMediaStore.dataDisplayName,
                videoSize = videoToInsertIntoMediaStore.dataSize,
                dataInputStream = DataInputStream(
                    BufferedInputStream(
                        context.contentResolver.openInputStream(
                            videoToInsertIntoMediaStore.dataUri
                        )
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
                        } else if (dataTransferStatus == DataToTransfer.TransferStatus.RECEIVE_ONGOING ||
                            dataTransferStatus == DataToTransfer.TransferStatus.RECEIVE_STARTED
                        ) {
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