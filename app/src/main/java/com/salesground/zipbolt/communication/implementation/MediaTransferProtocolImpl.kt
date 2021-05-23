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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.*
import java.lang.StringBuilder
import java.nio.ByteBuffer
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import kotlin.math.min
import kotlin.math.roundToInt

open class MediaTransferProtocolImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val advancedImageRepository: ImageRepository
) : MediaTransferProtocol {
    private var mTransferMetaData = MediaTransferProtocolMetaData.KEEP_RECEIVING
    private var ongoingTransfer = AtomicBoolean(false)
    private val buffer = ByteArray(1024 * 8)


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


        // write data type
        withContext(Dispatchers.IO) {
            dataOutputStream.writeInt(dataToTransfer.dataType)
            //  delay(50)
            // write data display name
            dataOutputStream.writeUTF(dataToTransfer.dataDisplayName)
            // delay(50)
            // write data size
            dataOutputStream.writeLong(dataToTransfer.dataSize)
        }
        // delay(50)

        var sizeOfBytesUnSent = dataToTransfer.dataSize

        context.contentResolver.openFileDescriptor(dataToTransfer.dataUri, "r")
            ?.also { parcelFileDescriptor ->
                val fileInputStream = DataInputStream(
                    BufferedInputStream(
                        FileInputStream(parcelFileDescriptor.fileDescriptor)
                    )
                )


                dataTransferListener(
                    dataToTransfer.dataDisplayName,
                    dataToTransfer.dataSize,
                    0f,
                    TransferState.TRANSFERING
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
                }


                /*var length: Int
                while (fileInputStream.read(buffer).also { length = it } > -1) {
                    // write the current transfer status to the peer.
                    // dataOutputStream.writeInt(mTransferMetaData.value)

                    when (mTransferMetaData) {
                        MediaTransferProtocolMetaData.CANCEL_ACTIVE_RECEIVE -> break
                        MediaTransferProtocolMetaData.KEEP_RECEIVING_BUT_CANCEL_ACTIVE_TRANSFER -> break
                    }

                    // write the bytes to be transferred
                    dataOutputStream.write(buffer, 0, length)
                    sizeOfBytesUnSent -= length
                    // send information about the number of bytes transferred
                    dataTransferListener(
                        dataToTransfer.dataDisplayName,
                        dataToTransfer.dataSize,
                        ((dataToTransfer.dataSize - sizeOfBytesUnSent) / dataToTransfer.dataSize.toFloat()) * 100f,
                        TransferState.TRANSFERING
                    )
                }*/

                //  dataOutputStream.flush()
                /*  while (sizeOfBytesUnSent > 0) {
                      // write the current transfer status to the peer.
                      dataOutputStream.writeInt(mTransferMetaData.value)
                      // delay(50)
                      when (mTransferMetaData) {
                          MediaTransferProtocolMetaData.CANCEL_ACTIVE_RECEIVE -> break
                          MediaTransferProtocolMetaData.KEEP_RECEIVING_BUT_CANCEL_ACTIVE_TRANSFER -> break
                      }
                      // read out bytes from the file into the bytes array
                      val sizeOfBytesRead = fileInputStream.read(buffer)

                      // write the size of bytes to be transferred
                      // dataOutputStream.writeInt(sizeOfBytesRead)
                      // delay(50)

                      // write the bytes to be transferred
                      dataOutputStream.write(buffer, 0, sizeOfBytesRead)
                      // delay(50)

                      // subtract the sizeOfBytesRead from the sizeOfBytesUnsent
                      sizeOfBytesUnSent -= sizeOfBytesRead

                      // send information about the number of bytes transferred
                      dataTransferListener(
                          dataToTransfer.dataDisplayName,
                          dataToTransfer.dataSize,
                          ((dataToTransfer.dataSize - sizeOfBytesUnSent) / dataToTransfer.dataSize.toFloat()) * 100f,
                          TransferState.TRANSFERING
                      )

                      /*  fileInputStream.read(buffer).also {
                            if (it == -1)
                            // write the number of bytes to be transferred
                                dataOutputStream.writeInt(it)
                            // write the bytes
                            dataOutputStream.write(buffer, 0, it)
                            // subtract the number of bytes transferred from the remaining data size
                            dataSize -= it

                            // send information about the number of bytes transferred
                            dataTransferListener(
                                dataToTransfer.dataDisplayName,
                                dataToTransfer.dataSize,
                                ((dataToTransfer.dataSize - dataSize) / dataToTransfer.dataSize.toFloat()) * 100f,
                                TransferState.TRANSFERING
                            )
                        }*/
                  }*/
                parcelFileDescriptor.close()
                fileInputStream.close()
                mTransferMetaData = MediaTransferProtocolMetaData.KEEP_RECEIVING
                ongoingTransfer.set(false)
            }
        //  dataOutputStream.flush()

    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun receiveMedia(
        dataInputStream: DataInputStream,
        bytesReceivedListener: (
            dataDisplayName: String, dataSize: Long, percentageOfDataRead: Float, dataType: Int,
            dataUri: Uri
        ) -> Unit
    ) {
        val mediaType = dataInputStream.readInt()
        // delay(50)
        val mediaName = dataInputStream.readUTF()
        // delay(50)
        val mediaSize = dataInputStream.readLong()
        // delay(50)

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
    }
}
