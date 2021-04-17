package com.salesground.zipbolt.communicationprotocol

import android.content.Context
import android.os.ParcelFileDescriptor
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.repository.ImageRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.FileInputStream
import javax.inject.Inject

class ZipBoltMediaTransferProtocol @Inject constructor(
    @ApplicationContext private val context: Context,
    private val imageRepository: ImageRepository
) : MediaTransferProtocol {

    private var mediaTransferListener: MediaTransferProtocol.MediaTransferListener? = null

    override fun setMediaTransferListener(
        mediaTransferListener:
        MediaTransferProtocol.MediaTransferListener
    ) {
        this.mediaTransferListener = mediaTransferListener
    }

    init {
        imageRepository.setImageByteReadListener(object : ImageRepository.ImageByteReadListener {
            override fun percentageOfBytesRead(bytesReadPercent: Pair<String, Float>) {
                mediaTransferListener?.percentageOfBytesTransferred(bytesReadPercent,
                transferState = MediaTransferProtocol.TransferState.RECEIVING)
            }
        })
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

            context.contentResolver.openFileDescriptor(dataToTransfer.dataUri, "r")
                ?.also { parcelFileDescriptor: ParcelFileDescriptor ->
                    val dataFileInputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
                    val bufferArray = ByteArray(10_000_000)
                    var dataSize = dataToTransfer.dataSize

                    mediaTransferListener?.percentageOfBytesTransferred(
                       bytesTransferred = Pair(
                            dataToTransfer.dataDisplayName,
                            0f
                        ),
                        transferState = MediaTransferProtocol.TransferState.TRANSFERING
                    )

                    while (dataSize > 0) {
                        dataSize -= dataFileInputStream.read(bufferArray).also {
                            dataOutputStream.write(bufferArray, 0, it)
                        }
                        try {
                            mediaTransferListener?.percentageOfBytesTransferred(
                                bytesTransferred = Pair(
                                    dataToTransfer.dataDisplayName,
                                    ((dataToTransfer.dataSize - dataSize) /
                                            dataToTransfer.dataSize) * 100f
                                ),
                                transferState = MediaTransferProtocol.TransferState.TRANSFERING
                            )
                        } catch (cannotDivideByZero: Exception) {

                        }
                    }
                    dataFileInputStream.close()
                }
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun receiveMedia(dataInputStream: DataInputStream) {
        withContext(Dispatchers.IO) {
            val mediaName = dataInputStream.readUTF()
            val mediaSize = dataInputStream.readLong()
            val mediaMimeType = dataInputStream.readUTF()

            when {
                mediaMimeType.contains("image", true) -> {
                    imageRepository.insertImageIntoMediaStore(
                        displayName = mediaName,
                        size = mediaSize,
                        mimeType = mediaMimeType,
                        dataInputStream = dataInputStream
                    )
                }
            }
        }
    }

}