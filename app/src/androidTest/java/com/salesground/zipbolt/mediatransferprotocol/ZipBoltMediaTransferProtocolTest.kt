package com.salesground.zipbolt.mediatransferprotocol

import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import com.salesground.zipbolt.communicationprotocol.MediaTransferProtocol
import com.salesground.zipbolt.repository.ImageRepository
import com.salesground.zipbolt.repository.SavedFilesRepository
import com.salesground.zipbolt.repository.ZipBoltMediaCategory
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.Assert
import java.io.*
import javax.inject.Inject

@HiltAndroidTest
class ZipBoltMediaTransferProtocolTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private val applicationContext = ApplicationProvider.getApplicationContext<Context>()

    @Inject
    lateinit var mediaTransferProtocol : MediaTransferProtocol

    @Inject
    lateinit var imageRepository: ImageRepository

    @Inject
    lateinit var savedFilesRepository: SavedFilesRepository


    @Before
    fun setUp(){
        hiltRule.inject()
    }

    @Test
    fun test_transferMedia() {
        runBlocking {
            val gateWay = File(savedFilesRepository.getZipBoltMediaCategoryBaseDirectory(categoryType = ZipBoltMediaCategory.IMAGES_BASE_DIRECTORY),
                "gateWay.txt")
            val dataOutputStream = DataOutputStream(FileOutputStream(gateWay))
            val dataInputStream = DataInputStream(FileInputStream(gateWay))

            mediaTransferProtocol.setMediaTransferListener(object : MediaTransferProtocol.MediaTransferListener{
                override fun percentageOfBytesTransferred(bytesTransferred: Float) {
                    Log.i("PercentageTransferred", bytesTransferred.toString())
                }
            })

            val imageToTransfer = imageRepository.
            getMetaDataOfImage(imageRepository.getImagesOnDevice(limit = 20)[19])
            mediaTransferProtocol.transferMedia(
                dataToTransfer =  imageToTransfer,
                dataOutputStream = dataOutputStream
            )
            val imageName = dataInputStream.readUTF()
            val parentFolder  = File(applicationContext.getExternalFilesDir(null), "Test Images")
            if(!parentFolder.exists()) parentFolder.mkdirs()
            val receivedImage = File(parentFolder, imageName)
            FileOutputStream(receivedImage).apply {
                var bytesUnread = dataInputStream.readLong()
                val mimeType = dataInputStream.readUTF()
                val buffer = ByteArray(1024)
                while(bytesUnread > 0){
                   bytesUnread -= dataInputStream.read(buffer).also {
                        write(buffer, 0, it)
                    }
                }
                flush()
                close()
            }
            assertEquals(receivedImage.length(), imageToTransfer.dataSize)
            assertEquals(receivedImage.name, imageToTransfer.dataDisplayName)
        }
    }
}