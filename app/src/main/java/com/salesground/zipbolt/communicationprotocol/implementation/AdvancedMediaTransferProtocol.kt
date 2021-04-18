package com.salesground.zipbolt.communicationprotocol.implementation

import android.content.Context
import com.salesground.zipbolt.communicationprotocol.MediaTransferProtocol
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.repository.ImageRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.FileInputStream
import javax.inject.Inject

class AdvancedMediaTransferProtocol @Inject constructor(
    @ApplicationContext private val context: Context,
    private val imageRepository: ImageRepository
) : MediaTransferProtocol {
    private var mTransferMetaData = MediaTransferProtocol.TransferMetaData.KEEP_RECEIVING
    private var dataFlowListener: (Pair<String, Float>, MediaTransferProtocol.TransferState) -> Unit =
        { _, _ ->

        }

    override fun setDataFlowListener(dataFlowListener: (Pair<String, Float>, MediaTransferProtocol.TransferState) -> Unit) {
        this.dataFlowListener = dataFlowListener
    }

    override fun cancelCurrentTransfer(transferMetaData: MediaTransferProtocol.TransferMetaData) {
        mTransferMetaData = transferMetaData
    }


    override fun setMediaTransferListener(mediaTransferListener: MediaTransferProtocol.MediaTransferListener) {

    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun transferMedia(
        dataToTransfer: DataToTransfer,
        dataOutputStream: DataOutputStream
    ) {
        withContext(Dispatchers.IO) {
            dataOutputStream.writeUTF(dataToTransfer.dataDisplayName)
            dataOutputStream.writeLong(dataToTransfer.dataSize)
            dataOutputStream.writeUTF(dataToTransfer.dataType)

            var dataSize = dataToTransfer.dataSize

            context.contentResolver.openFileDescriptor(dataToTransfer.dataUri, "r")
                ?.also { parcelFileDescriptor ->
                    val fileInputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
                    val buffer = ByteArray(10_000_000)

                    dataFlowListener(
                        Pair(
                            dataToTransfer.dataDisplayName, 0f
                        ), MediaTransferProtocol.TransferState.TRANSFERING
                    )

                    while (dataSize > 0) {
                        dataOutputStream.writeUTF(mTransferMetaData.status)
                        if (mTransferMetaData == MediaTransferProtocol.TransferMetaData.CANCEL_ACTIVE_RECEIVE) break
                        dataSize -= fileInputStream.read(buffer).also {
                            dataOutputStream.write(buffer, 0, it)
                            dataFlowListener(
                                Pair(
                                    dataToTransfer.dataDisplayName,
                                    ((dataToTransfer.dataSize - dataSize) / dataToTransfer.dataSize) * 100f
                                ),
                                MediaTransferProtocol.TransferState.TRANSFERING
                            )
                        }
                    }
                }
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun receiveMedia(dataInputStream: DataInputStream) {
        withContext(Dispatchers.IO){
            val mediaName = dataInputStream.readUTF()
            val mediaSize = dataInputStream.readLong()
            val mediaType = dataInputStream.readUTF()

            when{
                mediaType.contains("image", true) -> {

                }
            }
        }
    }
}