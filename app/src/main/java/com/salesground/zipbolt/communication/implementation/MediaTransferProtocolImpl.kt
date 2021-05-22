package com.salesground.zipbolt.communication.implementation

import android.content.Context
import android.net.Uri
import android.util.Log
import com.salesground.zipbolt.communication.DataTransferUtils
import com.salesground.zipbolt.communication.MediaTransferProtocol
import com.salesground.zipbolt.communication.MediaTransferProtocol.*
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.repository.ImageRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.*
import java.lang.StringBuilder
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

open class MediaTransferProtocolImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val advancedImageRepository: ImageRepository
) : MediaTransferProtocol {
    private var mTransferMetaData = MediaTransferProtocolMetaData.KEEP_RECEIVING
    private var ongoingTransfer = AtomicBoolean(false)


    override fun cancelCurrentTransfer(transferMetaData: MediaTransferProtocolMetaData) {
        if (ongoingTransfer.get()) mTransferMetaData = transferMetaData
    }


    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun transferMedia(
        dataToTransfer: DataToTransfer,
        dataOutputStream: DataOutputStream,
        dataTransferListener: (
            displayName: String, dataSize: Long, percentTransferred: Float,
            transferState: TransferState
        ) -> Unit
    ) {
        ongoingTransfer.set(true)

        dataOutputStream.writeLong(dataToTransfer.dataSize)
        // write name length
        dataOutputStream.writeInt(dataToTransfer.dataDisplayName.length)
        dataOutputStream.writeChars(dataToTransfer.dataDisplayName)

        dataOutputStream.writeInt(dataToTransfer.dataType)

        var dataSize = dataToTransfer.dataSize

        context.contentResolver.openFileDescriptor(dataToTransfer.dataUri, "r")
            ?.also { parcelFileDescriptor ->
                val fileInputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
                val buffer = ByteArray(10_000_000)

                dataTransferListener(
                    dataToTransfer.dataDisplayName,
                    dataToTransfer.dataSize,
                    0f,
                    TransferState.TRANSFERING
                )

                while (dataSize > 0) {
                    dataOutputStream.writeInt(mTransferMetaData.value)
                    when (mTransferMetaData) {
                        MediaTransferProtocolMetaData.CANCEL_ACTIVE_RECEIVE -> break
                        MediaTransferProtocolMetaData.KEEP_RECEIVING_BUT_CANCEL_ACTIVE_TRANSFER -> break
                    }

                    fileInputStream.read(buffer).also {
                        dataOutputStream.write(buffer, 0, it)
                        dataSize -= it
                        dataTransferListener(
                            dataToTransfer.dataDisplayName,
                            dataToTransfer.dataSize,
                            ((dataToTransfer.dataSize - dataSize) / dataToTransfer.dataSize.toFloat()) * 100f,
                            TransferState.TRANSFERING
                        )
                    }
                }
                parcelFileDescriptor.close()
                fileInputStream.close()
                mTransferMetaData = MediaTransferProtocolMetaData.KEEP_RECEIVING
                ongoingTransfer.set(false)
            }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun receiveMedia(
        dataInputStream: DataInputStream,
        bytesReceivedListener: (
            dataDisplayName: String, dataSize: Long, percentageOfDataRead: Float, dataType: Int,
            dataUri: Uri
        ) -> Unit
    ) {
        try {
            val mediaSize = dataInputStream.readLong()
            val mediaName = dataInputStream.readChars()

            when (val mediaType = dataInputStream.readInt()) {
                DataToTransfer.MediaType.IMAGE.value -> {
                    advancedImageRepository.insertImageIntoMediaStore(
                        displayName = mediaName,
                        size = mediaSize,
                        dataInputStream = dataInputStream,
                        transferMetaDataUpdateListener = {
                            if (it == MediaTransferProtocolMetaData.KEEP_RECEIVING_BUT_CANCEL_ACTIVE_TRANSFER) {
                                cancelCurrentTransfer(MediaTransferProtocolMetaData.KEEP_RECEIVING_BUT_CANCEL_ACTIVE_TRANSFER)
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
        }
    }

    private fun DataInputStream.readChars(): String {
        val charLength = readInt()
        val chars = CharArray(charLength)
        for(i in 0 until charLength){
            chars[i] = readChar()
        }
        return String(chars)
    }
}
