package com.salesground.zipbolt.mediatransferprotocol

import com.salesground.zipbolt.communicationprotocol.MediaTransferProtocol
import com.salesground.zipbolt.repository.ImageRepository
import com.salesground.zipbolt.repository.SavedFilesRepository
import com.salesground.zipbolt.repository.ZipBoltMediaCategory
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.DataOutputStream
import java.io.File
import javax.inject.Inject

@HiltAndroidTest
class ZipBoltMediaTransferProtocolTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

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
            val imageParentDirectory =
                savedFilesRepository.getZipBoltMediaCategoryBaseDirectory(categoryType = ZipBoltMediaCategory.IMAGES_BASE_DIRECTORY)
            val gateWay = File(imageParentDirectory, "gateWay.txt")
            val dataOutputStream = DataOutputStream()
            val imageToTransfer = imageRepository.getImagesOnDevice(limit = 2).first()
            mediaTransferProtocol.transferMedia(
                dataToTransfer =  imageToTransfer,
                dataOutputStream =
            )
        }
    }
}