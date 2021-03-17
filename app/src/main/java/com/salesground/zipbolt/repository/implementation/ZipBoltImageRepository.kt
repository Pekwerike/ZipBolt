package com.salesground.zipbolt.repository.implementation

import android.net.Uri
import androidx.core.net.toUri
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.model.DeviceImage
import com.salesground.zipbolt.repository.ImageRepository
import java.io.DataInputStream


class ZipBoltImageRepository : ImageRepository {
    override suspend fun insertImageIntoMediaStore(
        displayName: String,
        size: Long,
        mimeType: String,
        dataInputStream: DataInputStream
    ) {

    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun getAllImagesOnDevice(): MutableList<DataToTransfer> {
        val deviceImages = mutableListOf<DataToTransfer>()
        deviceImages.add(
            DataToTransfer.DeviceImage(
                imageUri = "".toUri(),
                imageDateModified = "",
                imageDisplayName = "",
                imageMimeType = "",
                imageBucketName = ""
            )
        )
        return deviceImages
    }

    override suspend fun getTenImagesOnDevice(): MutableList<DataToTransfer> {
        TODO("Not yet implemented")
    }
}