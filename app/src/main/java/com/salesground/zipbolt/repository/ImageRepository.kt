package com.salesground.zipbolt.repository

import android.net.Uri
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
    suspend fun insertImageIntoMediaStore(
        displayName: String,
        size: Long,
        mimeType: String,
        dataInputStream: DataInputStream
    )

    suspend fun getMetaDataOfImage(imageUri: Uri): DataToTransfer
    suspend fun getAllImagesOnDevice(): MutableList<DataToTransfer>
    suspend fun getTenImagesOnDevice(): MutableList<DataToTransfer>
}