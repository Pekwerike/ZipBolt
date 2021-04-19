package com.salesground.zipbolt.communicationprotocol.implementation

import android.content.Context
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import com.salesground.zipbolt.communicationprotocol.MediaTransferProtocol
import com.salesground.zipbolt.repository.ImageRepository
import com.salesground.zipbolt.repository.SavedFilesRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.*
import javax.inject.Inject

@HiltAndroidTest
class AdvanceMediaTransferProtocolTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val baseTestFolder = File(context.getExternalFilesDir(null), "Testing")
    private val gateWay = File(baseTestFolder, "gateway.txt")
    private val gateWayOutputStream = DataOutputStream(FileOutputStream(gateWay))
    private val gateWayInputStream = DataInputStream(FileInputStream(gateWay))
    private var imagesToCancelTransfer : MutableList<String> = mutableListOf()

    @Inject
    lateinit var savedFilesRepository: SavedFilesRepository

    @Inject
    lateinit var imageRepository: ImageRepository

    @Inject
    lateinit var advanceMediaTransferProtocol : AdvanceMediaTransferProtocol


    @Before
    fun setUp() {
        hiltRule.inject()
        advanceMediaTransferProtocol.setDataFlowListener{ dataInTransfer, transferState ->
            Log.i("TransferTest", "${transferState.name} - ${dataInTransfer.first}: ${dataInTransfer.second}")
            if(imagesToCancelTransfer.contains(dataInTransfer.first) &&
                dataInTransfer.second.coerceIn(49f, 99f) == dataInTransfer.second){
                advanceMediaTransferProtocol.cancelCurrentTransfer(
                    transferMetaData = MediaTransferProtocol.TransferMetaData.CANCEL_ACTIVE_RECEIVE
                )
            }
        }
    }

    @Test
    fun test_transfer_and_receive() = runBlocking {
        val allImagesOnDevice = imageRepository.getImagesOnDevice()
        imagesToCancelTransfer = mutableListOf<String>(
            allImagesOnDevice[0].dataDisplayName,
            allImagesOnDevice[1].dataDisplayName
        )
        allImagesOnDevice.forEach {
            advanceMediaTransferProtocol.transferMedia(
                dataToTransfer = it,
                dataOutputStream = gateWayOutputStream
            )
            delay(200)
            advanceMediaTransferProtocol.receiveMedia(
                dataInputStream = gateWayInputStream
            )
        }
        assertEquals(((allImagesOnDevice.size * 2) - 2), imageRepository.getImagesOnDevice().size)
    }

    @After
    fun tearDown() {
        gateWay.delete()
        baseTestFolder.delete()
    }
}