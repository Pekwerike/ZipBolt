package com.salesground.zipbolt.communication.implementation

import android.content.Context
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import com.salesground.zipbolt.communication.MediaTransferProtocol
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.repository.ZipBoltSavedFilesRepository
import com.salesground.zipbolt.repository.implementation.ZipBoltFileRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import java.io.*

class PlainFileMediaTransferProtocolTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val gateWayFile = File(
        context.getExternalFilesDir(null),
        "gateWay.txt"
    )
    private val zipBoltFileRepository: ZipBoltFileRepository = ZipBoltFileRepository(context)
    private val gateWayFileOutputStream =
        DataOutputStream(BufferedOutputStream(FileOutputStream(gateWayFile)))
    private val gateWayFileInputStream =
        DataInputStream(BufferedInputStream(FileInputStream(gateWayFile)))

    private val plainFileMediaTransferProtocol: PlainFileMediaTransferProtocol =
        PlainFileMediaTransferProtocol(ZipBoltSavedFilesRepository())

    @Test
    fun transferPlainFile() {
        runBlocking {
            val rootDir = zipBoltFileRepository.getRootDirectory()
            val rootDirChildren = zipBoltFileRepository.getDirectoryChildren(rootDir.path)
            rootDirChildren?.forEach {
                if (it.dataDisplayName == "Pictures") {
                    it as DataToTransfer.DeviceFile
                    val picturesChildren = zipBoltFileRepository.getDirectoryChildren(it.file.path)
                    picturesChildren.forEach {
                        it as DataToTransfer.DeviceFile
                        if (!it.file.isDirectory) {
                            plainFileMediaTransferProtocol.transferFile(
                                it,
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
                            plainFileMediaTransferProtocol.receivePlainFile(
                                gateWayFileInputStream,
                                object : MediaTransferProtocol.DataReceiveListener {
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
                                object : MediaTransferProtocol.TransferMetaDataUpdateListener {
                                    override fun onMetaTransferDataUpdate(
                                        mediaTransferProtocolMetaData: MediaTransferProtocol.MediaTransferProtocolMetaData
                                    ) {

                                    }
                                },
                                gateWayFileInputStream.readInt()
                            )
                        }
                    }
                }
            }
        }
    }

}