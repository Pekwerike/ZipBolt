package com.salesground.zipbolt.fakerepository

import androidx.core.net.toUri
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.repository.ImageRepository
import java.io.DataInputStream

class FakeZipBoltImageRepository : ImageRepository {
    private val allImagesOnDevice = mutableListOf<DataToTransfer>(
        DataToTransfer.DeviceImage(
            imageId = 1,
            imageUri = "content://externalstorage//images/01".toUri(),
            imageDateModified = "March 2021",
            imageDisplayName = "IMG-120394.jpeg",
            imageMimeType = "<image>/jpeg",
            imageSize = 102042L,
            imageBucketName = "Camera"
        ),
        DataToTransfer.DeviceImage(
            imageId = 2,
            imageUri = "content://externalstorage//images/02".toUri(),
            imageDateModified = "March 2021",
            imageDisplayName = "IMG-1203942.jpeg",
            imageMimeType = "<image>/jpeg",
            imageSize = 1020422L,
            imageBucketName = "ZipBolt"
        ),
        DataToTransfer.DeviceImage(
            imageId = 3,
            imageUri = "content://externalstorage//images/03".toUri(),
            imageDateModified = "March 2021",
            imageDisplayName = "IMG-1203934.jpeg",
            imageMimeType = "<image>/jpeg",
            imageSize = 10204234L,
            imageBucketName = "Camera"
        ),
        DataToTransfer.DeviceImage(
            imageId = 4,
            imageUri = "content://externalstorage//images/04".toUri(),
            imageDateModified = "March 2021",
            imageDisplayName = "IMG-1203944.jpeg",
            imageMimeType = "<image>/jpeg",
            imageSize = 1020452L,
            imageBucketName = "Camera"
        )
    )
    override suspend fun insertImageIntoMediaStore(
        displayName: String,
        size: Long,
        mimeType: String,
        dataInputStream: DataInputStream
    ) {

    }

    override suspend fun getMetaDataOfImage(image: DataToTransfer): DataToTransfer {
        return allImagesOnDevice[1]
    }

    override suspend fun getImagesOnDevice(limit: Int): MutableList<DataToTransfer> {
        return allImagesOnDevice.take(limit).toMutableList()
    }
}