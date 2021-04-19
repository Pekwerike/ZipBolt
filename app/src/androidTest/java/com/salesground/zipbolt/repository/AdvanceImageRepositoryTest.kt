package com.salesground.zipbolt.repository

import android.content.Context
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import com.salesground.zipbolt.communicationprotocol.MediaTransferProtocol
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.repository.implementation.AdvanceImageRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.*
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

    @Before
    fun setUp() {
        hiltRule.inject()
        val baseDirectory = File(
            context.getExternalFilesDir(null),
            "Testing"
        )
        if (!baseDirectory.exists()) baseDirectory.mkdirs()
        val gateWay = File(baseDirectory, "gateWay.txt")
        gateWayOutputStream = DataOutputStream(FileOutputStream(gateWay))
        gateWayInputStream = DataInputStream(FileInputStream(gateWay))
    }

    @Test
    fun test_insertImageIntoMediaStore() {
        runBlocking {

            val currentNumberOfImages = advanceImageRepository.getImagesOnDevice().size
            val imageToTransfer = advanceImageRepository.getMetaDataOfImage(
                advanceImageRepository.getImagesOnDevice(limit = 2).first() as DataToTransfer.DeviceImage
            )

            advanceImageRepository.setImageBytesReadListener {
                Log.i("BytesRead", "${it.first}: ${it.second}")
            }

            context.contentResolver.openFileDescriptor(imageToTransfer.dataUri, "r")
                ?.also { parcelFileDescriptor ->
                    val imageInputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
                    val buffer = ByteArray(10)
                    var unwrittenBytes = imageToTransfer.dataSize

                    while (unwrittenBytes > 0) {
                        gateWayOutputStream.writeUTF(MediaTransferProtocol.TransferMetaData.KEEP_RECEIVING.status)
                        imageInputStream.read(buffer).also {
                            unwrittenBytes -= it
                            gateWayOutputStream.write(buffer, 0, it)
                        }
                    }
                }
            advanceImageRepository.insertImageIntoMediaStore(
                displayName = imageToTransfer.dataDisplayName,
                size = imageToTransfer.dataSize,
                mimeType = imageToTransfer.dataType,
                dataInputStream = gateWayInputStream
            )
            assertEquals(currentNumberOfImages + 1, advanceImageRepository.getImagesOnDevice().size)
        }
    }
}