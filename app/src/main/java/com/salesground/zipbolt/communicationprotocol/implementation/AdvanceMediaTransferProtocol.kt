package com.salesground.zipbolt.communicationprotocol.implementation

import android.content.Context
import com.salesground.zipbolt.communicationprotocol.MediaTransferProtocol
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.repository.implementation.AdvanceImageRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

class AdvanceMediaTransferProtocol @Inject constructor(
    @ApplicationContext private val context: Context,
    private val advancedImageRepository: AdvanceImageRepository
) : MediaTransferProtocol {
    private var mTransferMetaData = MediaTransferProtocol.TransferMetaData.KEEP_RECEIVING
    private var ongoingTransfer = AtomicBoolean(false)
    private var dataFlowListener: (Pair<String, Float>, MediaTransferProtocol.TransferState) -> Unit =
        { _, _ ->

        }

    init {
        advancedImageRepository.setImageBytesReadListener {
            dataFlowListener(
                it,
                MediaTransferProtocol.TransferState.RECEIVING
            )
        }

        advancedImageRepository.setTransferMetaDataUpdateListener {
            when (it) {
                MediaTransferProtocol.TransferMetaData.KEEP_RECEIVING_BUT_CANCEL_ACTIVE_TRANSFER -> {
                    cancelCurrentTransfer(MediaTransferProtocol.TransferMetaData.KEEP_RECEIVING_BUT_CANCEL_ACTIVE_TRANSFER)
                }
            }
        }
    }

    override fun setDataFlowListener(dataFlowListener: (Pair<String, Float>, MediaTransferProtocol.TransferState) -> Unit) {
        this.dataFlowListener = dataFlowListener
    }

    override fun cancelCurrentTransfer(transferMetaData: MediaTransferProtocol.TransferMetaData) {
        if (ongoingTransfer.get()) mTransferMetaData = transferMetaData
    }


    override fun setMediaTransferListener(mediaTransferListener: MediaTransferProtocol.MediaTransferListener) {

    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun transferMedia(
        dataToTransfer: DataToTransfer,
        dataOutputStream: DataOutputStream
    ) {
        withContext(Dispatchers.IO) {
            ongoingTransfer.set(true)
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

                        when (mTransferMetaData) {
                            MediaTransferProtocol.TransferMetaData.CANCEL_ACTIVE_RECEIVE -> break
                            MediaTransferProtocol.TransferMetaData.KEEP_RECEIVING_BUT_CANCEL_ACTIVE_TRANSFER -> break
                        }

                        fileInputStream.read(buffer).also {
                            dataOutputStream.write(buffer, 0, it)
                            dataSize -= it
                            dataFlowListener(
                                Pair(
                                    dataToTransfer.dataDisplayName,
                                    ((dataToTransfer.dataSize - dataSize) / dataToTransfer.dataSize.toFloat()) * 100f
                                ),
                                MediaTransferProtocol.TransferState.TRANSFERING
                            )
                        }
                    }
                    mTransferMetaData = MediaTransferProtocol.TransferMetaData.KEEP_RECEIVING
                    ongoingTransfer.set(false)
                }
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun receiveMedia(dataInputStream: DataInputStream) {
        withContext(Dispatchers.IO) {
            try {
                val mediaName = dataInputStream.readUTF()
                val mediaSize = dataInputStream.readLong()
                val mediaType = dataInputStream.readUTF()

                when {
                    mediaType.contains("image", true) -> {
                        advancedImageRepository.insertImageIntoMediaStore(
                            displayName = mediaName,
                            size = mediaSize,
                            mimeType = mediaType,
                            dataInputStream = dataInputStream
                        )
                    }
                }
            }catch (endOfFileException: EOFException){
                return@withContext
            }catch (malformedInput: UTFDataFormatException){
                return@withContext
            }
        }}
}