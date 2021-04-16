package com.salesground.zipbolt.repository

import android.net.Uri
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.repository.implementation.ZipBoltImageRepository
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

    interface ImageByteReadListener {
        fun percentageOfBytesRead(bytesReadPercent: Float)
    }

    suspend fun insertImageIntoMediaStore(
        displayName: String,
        size: Long,
        mimeType: String,
        dataInputStream: DataInputStream
    )

    suspend fun getMetaDataOfImage(image: DataToTransfer): DataToTransfer
    suspend fun getImagesOnDevice(limit: Int = 0): MutableList<DataToTransfer>

    fun setImageByteReadListener(byteReadListener: ImageByteReadListener)
}
