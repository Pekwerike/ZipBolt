package com.salesground.zipbolt.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import com.salesground.zipbolt.communicationprotocol.MediaTransferProtocol
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.repository.implementation.AdvanceImageRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.*
import java.lang.StringBuilder
import javax.inject.Inject

@HiltAndroidTest
class AdvanceImageRepositoryTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var advanceImageRepository: AdvanceImageRepository

    private val context = ApplicationProvider.getApplicationContext<Context>()


    private lateinit var gateWayOutputStream: DataOutputStream
    private lateinit var gateWayInputStream: DataInputStream
    private lateinit var gateWayTwo : File
    private lateinit var gateWay : File
    private lateinit var baseDirectory: File
    private lateinit var gateWayOutputStreamTwo: DataOutputStream
    private lateinit var gateWayInputStreamTwo: DataInputStream


    @Before
    fun setUp() {
        hiltRule.inject()
        baseDirectory = File(
            context.getExternalFilesDir(null),
            "Testing"
        )
        if (!baseDirectory.exists()) baseDirectory.mkdirs()
        gateWay = File(baseDirectory, "gateWay.txt")
        gateWayTwo = File(baseDirectory, "gateWayTwo.txt")
        gateWayOutputStream = DataOutputStream(FileOutputStream(gateWay))
        gateWayInputStream = DataInputStream(FileInputStream(gateWay))
        gateWayOutputStreamTwo = DataOutputStream(FileOutputStream(gateWayTwo))
        gateWayInputStreamTwo = DataInputStream(FileInputStream(gateWayTwo))
    }

    @After
    fun shoutDown(){
        gateWayTwo.delete()
        gateWay.delete()
        baseDirectory.delete()
    }

    @Test
    fun insertImageIntoMediaStoreAndCancelFirstTransfer() = runBlocking {
            val numberOfImagesOnDevice = advanceImageRepository.getImagesOnDevice().size
            val firstImage = advanceImageRepository.getMetaDataOfImage(
                advanceImageRepository.getImagesOnDevice(limit = 3)[1] as DataToTransfer.DeviceImage
            )
            val secondImage = advanceImageRepository.getMetaDataOfImage(
                advanceImageRepository.getImagesOnDevice(limit = 3)[0] as DataToTransfer.DeviceImage
            )

            // write first image and cancel transfer
            context.contentResolver.openFileDescriptor(firstImage.dataUri, "r")
                ?.let { parcelFileDescriptor ->
                    val bytesUnwritten = firstImage.dataSize

                    while (bytesUnwritten > 0) {
                        gateWayOutputStream.writeUTF(MediaTransferProtocol.TransferMetaData.CANCEL_ACTIVE_RECEIVE.status)
                        break
                    }
                }

            // write second image
            transferData(secondImage)
            advanceImageRepository.insertImageIntoMediaStore(
                displayName = firstImage.dataDisplayName,
                size = firstImage.dataSize,
                mimeType = firstImage.dataType,
                dataInputStream = gateWayInputStream,
                transferMetaDataUpdateListener = {
                    Log.i("TransferMetaData", it.status)
                },
                bytesReadListener = {imageDisplayName: String, percentageOfDataRead: Float, imageUri: Uri ->
                    val logMessage = StringBuilder().apply {
                        append("DisplayName: $imageDisplayName \n")
                        append("PercentageOfDataRead: $percentageOfDataRead \n")
                        append("ImageUri: $imageUri")
                    }
                    Log.i("ImageBytesRead", logMessage.toString())
                }
            )
            advanceImageRepository.insertImageIntoMediaStore(
                displayName = secondImage.dataDisplayName,
                size = secondImage.dataSize,
                mimeType = secondImage.dataType,
                dataInputStream = gateWayInputStream,
                transferMetaDataUpdateListener = {
                    Log.i("TransferMetaData", it.status)
                },
                bytesReadListener = {imageDisplayName: String, percentageOfDataRead: Float, imageUri: Uri ->
                    val logMessage = StringBuilder().apply {
                        append("DisplayName: $imageDisplayName \n")
                        append("PercentageOfDataRead: $percentageOfDataRead \n")
                        append("ImageUri: $imageUri")
                    }
                    Log.i("ImageBytesRead", logMessage.toString())
                }
            )
            assertEquals(numberOfImagesOnDevice + 1, advanceImageRepository.getImagesOnDevice().size)
        }

    private fun transferData(secondImage: DataToTransfer) {
        context.contentResolver.openFileDescriptor(secondImage.dataUri, "r")
            ?.let { parcelFileDescriptor ->
                val secondImageInputStream =
                    FileInputStream(parcelFileDescriptor.fileDescriptor)
                val buffer = ByteArray(10_000_000)
                var unwrittenBytes = secondImage.dataSize
                while (unwrittenBytes > 0) {
                    gateWayOutputStream.writeUTF(MediaTransferProtocol.TransferMetaData.KEEP_RECEIVING.status)
                    secondImageInputStream.read(buffer).also {
                        gateWayOutputStream.write(buffer, 0, it)
                        unwrittenBytes -= it
                    }
                }
            }
    }
}