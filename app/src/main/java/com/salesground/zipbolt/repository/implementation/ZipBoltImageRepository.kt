package com.salesground.zipbolt.repository.implementation

import android.net.Uri
import androidx.core.net.toUri
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.repository.ImageRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.DataInputStream
import javax.inject.Inject


class ZipBoltImageRepository @Inject constructor(
    @ApplicationContext
    applicationContext: ApplicationContext
) : ImageRepository {
    override suspend fun insertImageIntoMediaStore(
        displayName: String,
        size: Long,
        mimeType: String,
        dataInputStream: DataInputStream
    ) {

    }

    override suspend fun getAllImagesOnDevice(): MutableList<DataToTransfer> {
        val deviceImages = mutableListOf<DataToTransfer>()


        return deviceImages
    }

    override suspend fun getTenImagesOnDevice(): MutableList<DataToTransfer> {
        TODO("Not yet implemented")
    }
}