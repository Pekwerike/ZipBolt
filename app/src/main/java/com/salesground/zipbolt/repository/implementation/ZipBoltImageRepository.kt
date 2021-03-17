package com.salesground.zipbolt.repository.implementation

import android.net.Uri
import android.os.Build
import android.provider.MediaStore
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
        val collection = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        }else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }
        val projection = arrayOf()

        return deviceImages
    }

    override suspend fun getTenImagesOnDevice(): MutableList<DataToTransfer> {
        TODO("Not yet implemented")
    }

    override suspend fun getMetaDataOfImage(imageUri: Uri): DataToTransfer {
        TODO("Not yet implemented")
    }
}