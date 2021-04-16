package com.salesground.zipbolt.communicationprotocol

import android.content.Context
import android.os.ParcelFileDescriptor
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.repository.ImageRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.FileInputStream
import javax.inject.Inject

class ZipBoltMediaTransferProtocol @Inject constructor(
    @ApplicationContext private val context: Context,
    private val imageRepository: ImageRepository
) : MediaTransferProtocol {

    private var mediaTransferredListener: MediaTransferProtocol.MediaTransferListener? = null

    override fun setMediaTransferredListener(mediaTransferredListener: MediaTransferProtocol.MediaTransferListener) {
        this.mediaTransferredListener = mediaTransferredListener
    }

    //TODO write tests for this function
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
                    var lengthOfDataRead: Int
                    var dataSize = dataToTransfer.dataSize


                    mediaTransferredListener?.percentageOfBytesTransfered(0f)

                    /*  while(dataSize > 0){
                          dataSize -= dataFileInputStream.read(bufferArray).also { lengthOfDataRead = it }
                          dataOutputStream.write(bufferArray, 0, lengthOfDataRead)
                      }*/

                    while (dataFileInputStream.read(bufferArray)
                            .also {
                                lengthOfDataRead = it
                                dataSize -= it
                            } > 0
                    ) {
                        dataOutputStream.write(bufferArray, 0, lengthOfDataRead)
                        mediaTransferredListener?.percentageOfBytesTransfered(
                            ((dataToTransfer.dataSize - dataSize) /
                                    dataToTransfer.dataSize) * 100f
                        )
                    }
                    dataFileInputStream.close()
                }
        }
    }

    override suspend fun receiveMedia(dataInputStream: DataInputStream) {

    }

}