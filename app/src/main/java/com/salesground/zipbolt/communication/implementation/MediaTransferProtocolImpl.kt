package com.salesground.zipbolt.communication.implementation

import android.content.Context
import android.net.Uri
import com.salesground.zipbolt.communication.MediaTransferProtocol
import com.salesground.zipbolt.communication.MediaTransferProtocol.*
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.repository.ImageRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import kotlin.math.min

open class MediaTransferProtocolImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val advancedImageRepository: ImageRepository
) : MediaTransferProtocol {
    private var mTransferMetaData = MediaTransferProtocolMetaData.KEEP_RECEIVING
    private var ongoingTransfer = AtomicBoolean(false)
    private val buffer = ByteArray(1024 * 8)
    private var dataToTransfer: DataToTransfer? = null


    override fun cancelCurrentTransfer(transferMetaData: MediaTransferProtocolMetaData) {
        if (ongoingTransfer.get()) mTransferMetaData = transferMetaData
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun transferMedia(
        dataToTransfer: DataToTransfer,
        dataOutputStream: DataOutputStream,
        dataTransferListener: (
            dataToTransfer: DataToTransfer,
            percentTransferred: Float,
            transferStatus: DataToTransfer.TransferStatus
        ) -> Unit
    ) {
        ongoingTransfer.set(true)
        this.dataToTransfer = dataToTransfer
        dataOutputStream.writeInt(dataToTransfer.dataType)
        dataOutputStream.writeUTF(dataToTransfer.dataDisplayName)
        dataOutputStream.writeLong(dataToTransfer.dataSize)


        context.contentResolver.openFileDescriptor(dataToTransfer.dataUri, "r")
            ?.also { parcelFileDescriptor ->
                val fileInputStream = DataInputStream(
                    BufferedInputStream(
                        FileInputStream(parcelFileDescriptor.fileDescriptor)
                    )
                )


                dataTransferListener(
                   this.dataToTransfer!!,
                    0f,
                    DataToTransfer.TransferStatus.TRANSFER_ONGOING
                )

                var lengthRead: Int
                var lengthUnread = dataToTransfer.dataSize
                fileInputStream.readFully(
                    buffer, 0, min(lengthUnread, buffer.size.toLong())
                        .toInt()
                ).also {
                    lengthRead = min(lengthUnread, buffer.size.toLong())
                        .toInt()
                    lengthUnread -= lengthRead
                    dataOutputStream.writeInt(mTransferMetaData.value)
                    dataOutputStream.write(buffer, 0, lengthRead)
                }

                while (lengthUnread > 0) {
                    fileInputStream.readFully(
                        buffer, 0,
                        min(lengthUnread, buffer.size.toLong()).toInt()
                    ).also {
                        lengthRead = min(lengthUnread, buffer.size.toLong()).toInt()
                        lengthUnread -= lengthRead
                    }
                    dataOutputStream.writeInt(mTransferMetaData.value)
                    when (mTransferMetaData) {
                        MediaTransferProtocolMetaData.CANCEL_ACTIVE_RECEIVE -> break
                        MediaTransferProtocolMetaData.KEEP_RECEIVING_BUT_CANCEL_ACTIVE_TRANSFER -> break
                    }
                    dataOutputStream.write(buffer, 0, lengthRead)

                    dataTransferListener(
                        this.dataToTransfer!!,
                        ((dataToTransfer.dataSize - lengthUnread) / dataToTransfer.dataSize.toFloat()) * 100f,
                        DataToTransfer.TransferStatus.TRANSFER_ONGOING
                    )
                }

                dataTransferListener(
                    this.dataToTransfer!!,
                    100f,
                    DataToTransfer.TransferStatus.TRANSFER_COMPLETE
                )

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
            dataUri: Uri?
        ) -> Unit
    ) {
        val mediaType = dataInputStream.readInt()
        val mediaName = dataInputStream.readUTF()
        val mediaSize = dataInputStream.readLong()

        when (mediaType) {
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
                    bytesReadListener = { imageDisplayName: String, imageSize: Long, percentageOfDataRead: Float, imageUri: Uri? ->
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
    }
}
