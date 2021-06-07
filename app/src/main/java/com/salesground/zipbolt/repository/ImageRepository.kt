package com.salesground.zipbolt.repository

import android.net.Uri
import com.salesground.zipbolt.communication.MediaTransferProtocol
import com.salesground.zipbolt.communication.MediaTransferProtocol.*
import com.salesground.zipbolt.model.DataToTransfer
import java.io.DataInputStream

/**
 * Functions description
 * A. insertImageIntoMediaStore
 * B. getMetaDataOfImage -> This function fetches the following details of an image Uri
 *        1. image mimeType
 *        2. image size
 *        3. image display name
 *        These details above will be used for socket communication when transfering the image
 * C. getAllImagesOnDevice
 * D. getTenImagesOnDevice
 *
 */
interface ImageRepository {


    interface TransferMetaDataUpdateListener {
        fun onMetaTransferDataUpdate(mediaTransferProtocolMetaData: MediaTransferProtocolMetaData)
    }

    interface BytesReadListener {
        fun onByteRead(
            imageDisplayName: String, imageSize: Long, percentageOfDataRead: Float, imageUri: Uri?,
            dataTransferStatus: DataToTransfer.TransferStatus
        )
    }

    suspend fun insertImageIntoMediaStore(
        displayName: String,
        size: Long,
        dataInputStream: DataInputStream,
        transferMetaDataUpdateListener: TransferMetaDataUpdateListener,
        bytesReadListener: BytesReadListener
    )

    suspend fun getMetaDataOfImage(image: DataToTransfer.DeviceImage): DataToTransfer
    suspend fun getImagesOnDevice(limit: Int = 0): MutableList<DataToTransfer>

}
