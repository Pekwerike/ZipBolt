package com.salesground.zipbolt.communicationprotocol

import android.content.Context
import android.os.ParcelFileDescriptor
import com.salesground.zipbolt.model.DataToTransfer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.FileInputStream
import javax.inject.Inject

class ZipBoltMediaTransferProtocol @Inject constructor(
    @ApplicationContext private val context: Context,
    private val mediaTransferProtocol: MediaTransferProtocol
) : MediaTransferProtocol {

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

                    while (dataFileInputStream.read(bufferArray)
                            .also { lengthOfDataRead = it } > 0
                    ) {
                        dataOutputStream.write(bufferArray, 0, lengthOfDataRead)
                    }
                    dataFileInputStream.close()
                }
        }
    }

    override suspend fun receiveMedia(dataInputStream: DataInputStream) {

    }

}