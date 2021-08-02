package com.salesground.zipbolt.communication.implementation

import android.content.Context
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import com.salesground.zipbolt.communication.MediaTransferProtocol
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.repository.SavedFilesRepository
import com.salesground.zipbolt.repository.ZipBoltSavedFilesRepository
import com.salesground.zipbolt.repository.implementation.ZipBoltFileRepository
import com.salesground.zipbolt.utils.getDirectorySize
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.*

class DirectoryMediaTransferProtocolTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val zipBoltFileRepository: ZipBoltFileRepository = ZipBoltFileRepository()
    private val zipBoltSavedFilesRepository : ZipBoltSavedFilesRepository = ZipBoltSavedFilesRepository()
    private val gateWayFile = File(zipBoltSavedFilesRepository
        .getZipBoltMediaCategoryBaseDirectory(SavedFilesRepository.ZipBoltMediaCategory.FOLDERS_BASE_DIRECTORY), "gateWay.txt")
    private val gateWayFileOutputStream =
        DataOutputStream(BufferedOutputStream(FileOutputStream(gateWayFile)))
    private val gateWayFileInputStream =
        DataInputStream(BufferedInputStream(FileInputStream(gateWayFile)))
    private val directoryMediaTransferProtocol: DirectoryMediaTransferProtocol =
        DirectoryMediaTransferProtocol(
            ZipBoltSavedFilesRepository()
        )

    @Test
    fun transferMedia() {
        runBlocking {
            val rootDir = zipBoltFileRepository.getRootDirectory()
            val rootFileChildren = zipBoltFileRepository.getDirectoryChildren(rootDir.path) as List<DataToTransfer.DeviceFile>
            val dataRoot = rootFileChildren.find {
                it.dataDisplayName == "Android"
            }
            val dataFolder = zipBoltFileRepository.getDirectoryChildren(dataRoot!!.file.path)

            for (rootFileChild in dataFolder) {
                rootFileChild as DataToTransfer.DeviceFile
                if (rootFileChild.file.name == "data") {
                    directoryMediaTransferProtocol.transferMedia(
                        rootFileChild,
                        gateWayFileOutputStream,
                        object : MediaTransferProtocol.DataTransferListener {
                            override fun onTransfer(
                                dataToTransfer: DataToTransfer,
                                percentTransferred: Float,
                                transferStatus: DataToTransfer.TransferStatus
                            ) {

                            }
                        }
                    )
                    gateWayFileInputStream.readInt()
                    directoryMediaTransferProtocol.receiveMedia(
                        gateWayFileInputStream,
                        object: MediaTransferProtocol.DataReceiveListener{
                            override fun onReceive(
                                dataDisplayName: String,
                                dataSize: Long,
                                percentageOfDataRead: Float,
                                dataType: Int,
                                dataUri: Uri?,
                                dataTransferStatus: DataToTransfer.TransferStatus
                            ) {

                            }
                        },
                        gateWayFileInputStream.readUTF(),
                        gateWayFileInputStream.readLong()
                    )
                    break
                }
            }
        }
    }

    @Test
    fun receiveMedia() {

    }

}