package com.salesground.zipbolt.communication.implementation

import android.content.Context
import android.net.Uri
import android.util.Log
import com.salesground.zipbolt.communication.DataTransferUtils
import com.salesground.zipbolt.communication.MediaTransferProtocol
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.repository.ImageRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.*
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

open class MediaTransferProtocolImpl @Inject constructor(
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
        ongoingTransfer.set(true)

        DataTransferUtils.writeSocketString(dataToTransfer.dataDisplayName, dataOutputStream)
        dataOutputStream.writeLong(dataToTransfer.dataSize)
        DataTransferUtils.writeSocketString(dataToTransfer.dataType, dataOutputStream)

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
                    DataTransferUtils.writeSocketString(mTransferMetaData.status,
                    dataOutputStream)
                    //  dataOutputStream.writeUTF(mTransferMetaData.status)

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

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun receiveMedia(
        dataInputStream: DataInputStream,
        bytesReceivedListener: (
            dataDisplayName: String, dataSize: Long, percentageOfDataRead: Float, dataType: String,
            dataUri: Uri
        ) -> Unit
    ) {
        try {
            val mediaName = DataTransferUtils.readSocketString(dataInputStream)
            val mediaSize = dataInputStream.readLong()
            val mediaType = DataTransferUtils.readSocketString(dataInputStream)


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
            return
        } catch (malformedInput: UTFDataFormatException) {
            malformedInput.printStackTrace()
            return
        }
    }

}
