package com.salesground.zipbolt.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import com.bumptech.glide.Glide
import com.salesground.zipbolt.communication.MediaTransferProtocol
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.repository.implementation.ZipBoltVideoRepository
import com.salesground.zipbolt.utils.getVideoDuration
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
            ZipBoltSavedFilesRepository(),
            context
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
    fun shutDown() {
        //gateWayInputStream.close()
        //gateWayOutputStream.close()
        gateWayFile.delete()
    }


    @Test
    fun insertVideoIntoMediaStore() {
        runBlocking {
            val videoToInsertIntoMediaStore =
                zipBoltVideoRepository.getVideosOnDevice().first() as DataToTransfer.DeviceVideo

            // write the video file into the gateway file
            context.contentResolver.openInputStream(videoToInsertIntoMediaStore.dataUri)?.let {
                val fileDataInputStream = DataInputStream(BufferedInputStream(it))

                val buffer = ByteArray(1024 * 1024)
                var dataSize = videoToInsertIntoMediaStore.dataSize

                while (dataSize > 0) {
                    // write to the gate way output stream that the receiver can continue receiving
                    gateWayOutputStream.writeInt(MediaTransferProtocol.MediaTransferProtocolMetaData.KEEP_RECEIVING.value)

                    val currentReadSize = min(buffer.size.toLong(), dataSize).toInt()
                    // read out the video bytes from the video file input stream
                    fileDataInputStream.readFully(buffer, 0, currentReadSize)

                    // write out the video bytes to the gateWay output stream
                    gateWayOutputStream.write(buffer, 0, currentReadSize)
                    dataSize -= currentReadSize
                }
            }


            zipBoltVideoRepository.insertVideoIntoMediaStore(
                videoName = videoToInsertIntoMediaStore.dataDisplayName,
                videoSize = videoToInsertIntoMediaStore.dataSize,
                dataInputStream = gateWayInputStream,
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
                },
                videoDuration = videoToInsertIntoMediaStore.videoDuration
            )
        }
    }

    @Test
    fun getVideosOnDevice() {
        runBlocking {
            zipBoltVideoRepository.getVideosOnDevice().let {
                assert(it.isNotEmpty())
                it.forEach {
                    Log.i("VideosReceived", it.dataDisplayName)
                }
            }
        }
    }

    @Test
    fun getVideoDuration() {
        runBlocking {
            val videoToGetDuration =
                zipBoltVideoRepository.getVideosOnDevice()[1] as DataToTransfer.DeviceVideo
            val videoDuration = videoToGetDuration.dataUri.getVideoDuration(context)
            assertEquals(videoToGetDuration.videoDuration, videoDuration)
        }
    }

    @Test
    fun test_videoUriHoldsImage() {
        runBlocking {
            val video = zipBoltVideoRepository.getVideosOnDevice()[1]
            assert(Glide.with(context).load(video.dataUri).submit().get() != null)
        }
    }
}