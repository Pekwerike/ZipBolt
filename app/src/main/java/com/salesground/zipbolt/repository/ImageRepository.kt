package com.salesground.zipbolt.repository

import com.salesground.zipbolt.model.DataToTransfer
import java.io.DataInputStream

interface ImageRepository {
    suspend fun insertImageIntoMediaStore(
        displayName: String,
        size: Long,
        mimeType: String,
        dataInputStream: DataInputStream
    )

    suspend fun getAllImagesOnDevice(): MutableList<DataToTransfer>
    suspend fun getTenImagesOnDevice(): MutableList<DataToTransfer>
}