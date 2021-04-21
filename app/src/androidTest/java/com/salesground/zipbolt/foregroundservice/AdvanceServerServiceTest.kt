package com.salesground.zipbolt.foregroundservice

import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.test.core.app.ApplicationProvider
import androidx.test.rule.ServiceTestRule
import com.salesground.zipbolt.IS_SERVER_KEY
import com.salesground.zipbolt.SERVER_IP_ADDRESS_KEY
import com.salesground.zipbolt.communicationprotocol.MediaTransferProtocol
import com.salesground.zipbolt.communicationprotocol.implementation.AdvanceMediaTransferProtocol
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.repository.ImageRepository
import com.salesground.zipbolt.repository.SavedFilesRepository
import com.salesground.zipbolt.repository.ZipBoltSavedFilesRepository
import com.salesground.zipbolt.repository.implementation.AdvanceImageRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.*
import javax.inject.Inject

@HiltAndroidTest
class AdvanceServerServiceTest {

   /* private val context = ApplicationProvider.getApplicationContext<Context>()
    private lateinit var baseTestDirectory: File
    private lateinit var gateWayOne: File
    private lateinit var gateWayOneDOS: DataOutputStream
    private lateinit var gateWayOneDIS: DataInputStream
    private lateinit var gateWayTwo: File
    private lateinit var gateWayTwoDOS: DataOutputStream
    private lateinit var gateWayTwoDIS: DataInputStream

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val serviceRule = ServiceTestRule()

    @Inject
    lateinit var imageRepository: ImageRepository

    private val clientMediaTransferProtocol = AdvanceMediaTransferProtocol(context,
    AdvanceImageRepository(context, ZipBoltSavedFilesRepository())
    )

    private val serverMediaTransferProtocol = AdvanceMediaTransferProtocol(context,
        AdvanceImageRepository(context, ZipBoltSavedFilesRepository())
    )

    @Before
    fun setUp() {
        hiltRule.inject()
        baseTestDirectory = File(context.getExternalFilesDir(null), "BaseTestDirectory")
        if (!baseTestDirectory.exists()) baseTestDirectory.mkdirs()
        gateWayOne = File(baseTestDirectory, "gateWayOne.txt")
        gateWayOneDOS = DataOutputStream(FileOutputStream(gateWayOne))
        gateWayOneDIS = DataInputStream(FileInputStream(gateWayOne))
        gateWayTwo = File(baseTestDirectory, "gateWayTwo.txt")
        gateWayTwoDOS = DataOutputStream(FileOutputStream(gateWayTwo))
        gateWayTwoDIS = DataInputStream(FileInputStream(gateWayTwo))
    }

    @After
    fun tearDown() {
        gateWayOne.delete()
        gateWayTwo.delete()
        baseTestDirectory.delete()
    }

    @Test
    fun testBindService() = runBlocking {
        val imagesOnDevice = imageRepository.getImagesOnDevice().map {
            imageRepository.getMetaDataOfImage(it as DataToTransfer.DeviceImage)
        }
        val serverServiceIntent = Intent(context, DataTransferService::class.java).apply {
            putExtra(IS_SERVER_KEY, true)
        }
        val clientServiceIntent = Intent(context, DataTransferService::class.java).apply {
            putExtra(IS_SERVER_KEY, false)
            putExtra(SERVER_IP_ADDRESS_KEY, "192.168.43.190")
        }

        val serverServiceBinder: IBinder = serviceRule.bindService(serverServiceIntent)
        val clientServiceBinder: IBinder = serviceRule.bindService(clientServiceIntent)


        val serverService = (serverServiceBinder as
                DataTransferService.DataTransferServiceBinder).getServiceInstance()
        val clientService = (clientServiceBinder as
                DataTransferService.DataTransferServiceBinder).getServiceInstance()

        serverService.setUpMediaTransferProtocolForTestCase(serverMediaTransferProtocol)
        serverService.configureServerMock(
            dataOutputStream = gateWayOneDOS,
            dataInputStream = gateWayTwoDIS
        )

        clientService.setUpMediaTransferProtocolForTestCase(clientMediaTransferProtocol)
        clientService.configureClientMock(
            dataOutputStream = gateWayTwoDOS,
            dataInputStream = gateWayOneDIS
        )

        clientService.transferData(dataCollectionSelected =
        imagesOnDevice.takeLast(3).toMutableList())


        delay(20000)
        assertEquals(imagesOnDevice.size + 3, imageRepository.getImagesOnDevice().size)
    }*/
}