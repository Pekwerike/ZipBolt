package com.salesground.zipbolt.communication.implementation

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.test.core.app.ApplicationProvider
import com.salesground.zipbolt.communication.MediaTransferProtocol
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.repository.ImageRepository
import com.salesground.zipbolt.repository.SavedFilesRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.*
import java.lang.StringBuilder
import javax.inject.Inject

@HiltAndroidTest
class MediaTransferProtocolImplTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private lateinit var baseTestFolder: File
    private lateinit var gateWay: File
    private lateinit var gateWayOutputStream: DataOutputStream
    private lateinit var gateWayInputStream: DataInputStream
    private var imagesToCancelTransfer: MutableList<String> = mutableListOf()
    private val deletedImages: MutableList<String> = mutableListOf()

    private val localBroadcastManager = LocalBroadcastManager.getInstance(context)


    @Inject
    lateinit var savedFilesRepository: SavedFilesRepository

    @Inject
    lateinit var imageRepository: ImageRepository

    @Inject
    lateinit var mediaTransferProtocolImpl: MediaTransferProtocolImpl


    @Before
    fun setUp() {
        hiltRule.inject()
        baseTestFolder = File(context.getExternalFilesDir(null), "Testing")
        if (!baseTestFolder.exists()) baseTestFolder.mkdirs()
        gateWay = File(baseTestFolder, "gateway.txt")
        gateWayOutputStream = DataOutputStream(FileOutputStream(gateWay))
        gateWayInputStream = DataInputStream(FileInputStream(gateWay))
    }

    @Test
    fun test_transfer_and_receive() = runBlocking {

        val allImagesOnDevice = imageRepository.getImagesOnDevice().map {
            imageRepository.getMetaDataOfImage(it as DataToTransfer.DeviceImage)
        }
        imagesToCancelTransfer = mutableListOf(
            allImagesOnDevice[0].dataDisplayName,
            allImagesOnDevice[1].dataDisplayName,
            allImagesOnDevice[2].dataDisplayName
        )
        allImagesOnDevice.forEach {
            launch {
                mediaTransferProtocolImpl.transferMedia(
                    dataToTransfer = it,
                    dataOutputStream = gateWayOutputStream,
                    dataTransferListener = object : MediaTransferProtocol.DataTransferListener {
                        override fun onTransfer(
                            dataToTransfer: DataToTransfer,
                            percentTransferred: Float,
                            transferStatus: DataToTransfer.TransferStatus
                        ) {
                            val logMessage = StringBuilder().apply {
                                append("DisplayName: ${dataToTransfer.dataDisplayName} \n")
                                append("PercentageOfDataRead: ${dataToTransfer.percentTransferred} \n")
                                append("DataSize: ${dataToTransfer.dataSize}")
                            }
                            // Log.i("DataTransferred", logMessage.toString())
                        }
                    }
                )
            }
            delay(300)
            mediaTransferProtocolImpl.receiveMedia(
                dataInputStream = gateWayInputStream,
                dataReceiveListener = object : MediaTransferProtocol.DataReceiveListener {
                    override fun onReceive(
                        dataDisplayName: String,
                        dataSize: Long,
                        percentageOfDataRead: Float,
                        dataType: Int,
                        dataUri: Uri?,
                        dataTransferStatus: DataToTransfer.TransferStatus
                    ) {
                        if (dataTransferStatus == DataToTransfer.TransferStatus.RECEIVE_COMPLETE) {
                            assert(dataUri != null)
                        }
                    }
                }
            )
        }

        delay(6000)

        assertEquals(
            ((allImagesOnDevice.size * 2) - deletedImages.toSet().size),
            imageRepository.getImagesOnDevice().size
        )
    }

    @After
    fun tearDown() {
        gateWay.delete()
        baseTestFolder.delete()
    }
}