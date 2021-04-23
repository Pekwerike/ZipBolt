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
    private val advancedImageRepository: ImageRepository,
    private val localBroadcastManager: LocalBroadcastManager
) : MediaTransferProtocol {
    private var mTransferMetaData = MediaTransferProtocol.TransferMetaData.KEEP_RECEIVING
    private var ongoingTransfer = AtomicBoolean(false)

    private val incomingDataBroadcastIntent =
        Intent(IncomingDataBroadcastReceiver.INCOMING_DATA_BYTES_RECEIVED_ACTION)


    override fun setDataFlowListener(dataFlowListener: (Pair<String, Float>, MediaTransferProtocol.TransferState) -> Unit) {

    }

    override fun cancelCurrentTransfer(transferMetaData: MediaTransferProtocol.TransferMetaData) {
        if (ongoingTransfer.get()) mTransferMetaData = transferMetaData
    }


    override fun setMediaTransferListener(mediaTransferListener: MediaTransferProtocol.MediaTransferListener) {

    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun transferMedia(
        dataToTransfer: DataToTransfer,
        dataOutputStream: DataOutputStream,
        dataTransferListener: (Pair<String, Float>, MediaTransferProtocol.TransferState) -> Unit
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
                            dataTransferListener(
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
                            dataInputStream = dataInputStream,
                            transferMetaDataUpdateListener = {
                                when (it) {
                                    MediaTransferProtocol.TransferMetaData.KEEP_RECEIVING_BUT_CANCEL_ACTIVE_TRANSFER -> {
                                        cancelCurrentTransfer(MediaTransferProtocol.TransferMetaData.KEEP_RECEIVING_BUT_CANCEL_ACTIVE_TRANSFER)
                                    }
                                }
                            },
                            bytesReadListener = { pair: Pair<String, Float>, uri: Uri ->
                                incomingDataBroadcastIntent.apply {
                                    putExtra(IncomingDataBroadcastReceiver.INCOMING_FILE_NAME, pair.first)
                                    putExtra(
                                        IncomingDataBroadcastReceiver.PERCENTAGE_OF_DATA_RECEIVED,
                                        pair.second
                                    )
                                    putExtra(IncomingDataBroadcastReceiver.INCOMING_FILE_URI,
                                    uri)
                                }
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