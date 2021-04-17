package com.salesground.zipbolt.mediatransferprotocol

import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import com.salesground.zipbolt.communicationprotocol.MediaTransferProtocol
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.repository.ImageRepository
import com.salesground.zipbolt.repository.SavedFilesRepository
import com.salesground.zipbolt.repository.ZipBoltMediaCategory
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.*
import java.io.*
import javax.inject.Inject

@HiltAndroidTest
class ZipBoltMediaTransferProtocolTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private val applicationContext = ApplicationProvider.getApplicationContext<Context>()

    @Inject
    lateinit var mediaTransferProtocol: MediaTransferProtocol

    @Inject
    lateinit var imageRepository: ImageRepository

    @Inject
    lateinit var savedFilesRepository: SavedFilesRepository

    private lateinit var dataOutputStream: DataOutputStream
    private lateinit var dataInputStream: DataInputStream
    private lateinit var baseFolder : File
    private lateinit var gateWay: File

    @Before
    fun setUp() {
        hiltRule.inject()
        baseFolder = File(applicationContext.getExternalFilesDir(null), "Testing")
        if (!baseFolder.exists()) baseFolder.mkdirs()
        gateWay = File(
            baseFolder,
            "gateWay.txt"
        )

        dataOutputStream = DataOutputStream(FileOutputStream(gateWay))
        dataInputStream = DataInputStream(FileInputStream(gateWay))
    }
    
    @After
    fun tearDown(){
        gateWay.delete()
        baseFolder.delete()
    }

    @Test
    fun test_file_transfer() {
        runBlocking {
            val numberOfImagesInMediaStore = imageRepository.getImagesOnDevice().size

            mediaTransferProtocol.setMediaTransferListener(object :
                MediaTransferProtocol.MediaTransferListener {
                override fun percentageOfBytesTransferred(bytesTransferred:
                                                          Pair<String, Float>,
                transferState : MediaTransferProtocol.TransferState) {
                    when(transferState){
                        MediaTransferProtocol.TransferState.TRANSFERING -> {
                            Log.i(
                                "PercentTransferred",
                                "Transferring - ${bytesTransferred.first} : ${bytesTransferred.second}"
                            )
                        }
                        MediaTransferProtocol.TransferState.RECEIVING -> {
                            Log.i(
                                "PercentTransferred",
                                "Receiving - ${bytesTransferred.first} : ${bytesTransferred.second}"
                            )
                        }
                    }
                }
            })
            
            imageRepository.getImagesOnDevice().map {
                imageRepository.getMetaDataOfImage(it as DataToTransfer.DeviceImage)
            }.forEach { imageToTransfer ->
                //transfer image
                mediaTransferProtocol.transferMedia(
                    dataToTransfer = imageToTransfer,
                    dataOutputStream = dataOutputStream
                )
            }

            repeat(numberOfImagesInMediaStore) {
                //receive all transferred image
                mediaTransferProtocol.receiveMedia(
                    dataInputStream = dataInputStream
                )
            }
            assertEquals(imageRepository.getImagesOnDevice().size, numberOfImagesInMediaStore * 2)
        }
    }
}