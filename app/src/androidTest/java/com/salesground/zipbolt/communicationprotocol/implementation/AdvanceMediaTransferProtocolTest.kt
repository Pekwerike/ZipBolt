package com.salesground.zipbolt.communicationprotocol.implementation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.test.core.app.ApplicationProvider
import com.salesground.zipbolt.broadcast.IncomingDataBroadcastReceiver
import com.salesground.zipbolt.communicationprotocol.MediaTransferProtocol
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
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

@HiltAndroidTest
class AdvanceMediaTransferProtocolTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private lateinit var baseTestFolder: File
    private lateinit var gateWay: File
    private lateinit var gateWayOutputStream: DataOutputStream
    private lateinit var gateWayInputStream: DataInputStream
    private var imagesToCancelTransfer: MutableList<String> = mutableListOf()
    private val deletedImages: MutableList<String> = mutableListOf()

    private val incomingDataBroadcastReceiver = IncomingDataBroadcastReceiver()
    private val localBroadcastManager = LocalBroadcastManager.getInstance(context)
    private val incomingDataBroadcastIntent =
        Intent(IncomingDataBroadcastReceiver.INCOMING_DATA_BYTES_RECEIVED_ACTION)

    @Inject
    lateinit var savedFilesRepository: SavedFilesRepository

    @Inject
    lateinit var imageRepository: ImageRepository

    @Inject
    lateinit var advanceMediaTransferProtocol: AdvanceMediaTransferProtocol


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
                advanceMediaTransferProtocol.transferMedia(
                    dataToTransfer = it,
                    dataOutputStream = gateWayOutputStream,
                    dataTransferListener = { displayName: String, dataSize: Long, percentTransferred: Float, transferState: MediaTransferProtocol.TransferState ->
                        val logMessage = StringBuilder().apply {
                            append("DisplayName: $displayName \n")
                            append("PercentageOfDataRead: $percentTransferred \n")
                            append("DataSize: $dataSize")
                        }
                        // Log.i("DataTransferred", logMessage.toString())
                    }
                )
            }
            delay(300)
            advanceMediaTransferProtocol.receiveMedia(
                dataInputStream = gateWayInputStream,
                bytesReceivedListener = { dataDisplayName: String, dataSize: Long, percentageOfDataRead: Float, dataType: String, dataUri: Uri ->
                    incomingDataBroadcastIntent.apply {
                        putExtra(
                            IncomingDataBroadcastReceiver.INCOMING_FILE_NAME,
                            dataDisplayName
                        )
                        putExtra(IncomingDataBroadcastReceiver.INCOMING_FILE_URI, dataUri)
                        putExtra(
                            IncomingDataBroadcastReceiver.PERCENTAGE_OF_DATA_RECEIVED,
                            percentageOfDataRead
                        )
                        putExtra(
                            IncomingDataBroadcastReceiver.INCOMING_FILE_MIME_TYPE,
                            dataType
                        )
                        putExtra(IncomingDataBroadcastReceiver.INCOMING_FILE_SIZE, dataSize)
                        localBroadcastManager.sendBroadcast(this)
                    }
                    incomingDataBroadcastReceiver.onReceive(context, incomingDataBroadcastIntent)
                }
            )
        }

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