package com.salesground.zipbolt.communicationprotocol.implementation

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.salesground.zipbolt.broadcast.IncomingDataBroadcastReceiver
import com.salesground.zipbolt.communicationprotocol.MediaTransferProtocol
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.repository.ImageRepository
import com.salesground.zipbolt.repository.implementation.AdvanceImageRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

class AdvanceMediaTransferProtocol @Inject constructor(
    @ApplicationContext private val context: Context,
    private val advancedImageRepository: ImageRepository
) : MediaTransferProtocol {
    private var mTransferMetaData = MediaTransferProtocol.TransferMetaData.KEEP_RECEIVING
    private var ongoingTransfer = AtomicBoolean(false)


    override fun cancelCurrentTransfer(transferMetaData: MediaTransferProtocol.TransferMetaData) {
        if (ongoingTransfer.get()) mTransferMetaData = transferMetaData
    }


    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun transferMedia(
        dataToTransfer: DataToTransfer,
        dataOutputStream: DataOutputStream,
        dataTransferListener: (
            displayName: String, dataSize: Long, percentTransferred: Float,
            transferState: MediaTransferProtocol.TransferState
        ) -> Unit
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

                    dataTransferListener(
                        dataToTransfer.dataDisplayName,
                        dataToTransfer.dataSize,
                        0f,
                        MediaTransferProtocol.TransferState.TRANSFERING
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
                            dataTransferListener(
                                dataToTransfer.dataDisplayName,
                                dataToTransfer.dataSize,
                                ((dataToTransfer.dataSize - dataSize) / dataToTransfer.dataSize.toFloat()) * 100f,
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
    override suspend fun receiveMedia(
        dataInputStream: DataInputStream,
        bytesReceivedListener: (
            dataDisplayName: String, dataSize: Long, percentageOfDataRead: Float, dataType: String,
            dataUri: Uri
        ) -> Unit
    ) {
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
                            dataInputStream = dataInputStream,
                            transferMetaDataUpdateListener = {
                                if (it == MediaTransferProtocol.TransferMetaData.KEEP_RECEIVING_BUT_CANCEL_ACTIVE_TRANSFER) {
                                    cancelCurrentTransfer(MediaTransferProtocol.TransferMetaData.KEEP_RECEIVING_BUT_CANCEL_ACTIVE_TRANSFER)
                                }
                            },
                            bytesReadListener = { imageDisplayName: String, imageSize: Long, percentageOfDataRead: Float, imageUri: Uri ->
                                bytesReceivedListener(
                                    imageDisplayName,
                                    imageSize,
                                    percentageOfDataRead,
                                    mediaType,
                                    imageUri
                                )
                            }
                        )
                    }
                }
            } catch (endOfFileException: EOFException) {
                endOfFileException.printStackTrace()
                return@withContext
            } catch (malformedInput: UTFDataFormatException) {
                malformedInput.printStackTrace()
                return@withContext
            }
        }
    }
}