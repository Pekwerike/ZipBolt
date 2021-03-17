package com.salesground.zipbolt.repository.implementation

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.net.toUri
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.repository.ImageRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.DataInputStream
import java.io.File
import javax.inject.Inject


class ZipBoltImageRepository @Inject constructor(
    @ApplicationContext
    private val applicationContext: Context
) : ImageRepository {
    override suspend fun insertImageIntoMediaStore(
        displayName: String,
        size: Long,
        mimeType: String,
        dataInputStream: DataInputStream
    ) {

    }

    override fun getAllImagesOnDevice(): MutableList<DataToTransfer> {
        val deviceImages = mutableListOf<DataToTransfer>()
        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }
        val projection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATE_MODIFIED
            )
        } else {
            arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DATE_MODIFIED
            )
        }

        val sortOrder = "${MediaStore.Images.Media.DATE_MODIFIED} DESC"

        applicationContext.contentResolver.query(
            collection,
            projection,
            null,
            null,
            sortOrder
        )?.let { cursor ->
            val imageIdColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
            val imageDateModifiedColumnIndex =
                cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED)
            val imageBucketNameIndex = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            } else {
                cursor.getColumnIndex(MediaStore.Images.Media.DATA)
            }
            while (cursor.moveToNext()) {
                val imageId = cursor.getLong(imageIdColumnIndex)
                val imageDateModified = cursor.getLong(imageDateModifiedColumnIndex)
                val imageBucketDisplayName =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        cursor.getString(imageBucketNameIndex)
                    } else {
                        File(cursor.getString(imageBucketNameIndex)).parentFile!!.name
                    }

                deviceImages.add(
                    DataToTransfer.DeviceImage(
                        imageUri = ContentUris.withAppendedId(collection, imageId),
                        imageDateModified = imageDateModified.toString(), // todo, write a utils function that converts time to date
                        imageBucketName = imageBucketDisplayName
                    )
                )
            }
        }
        return deviceImages
    }

    override suspend fun getTenImagesOnDevice(): MutableList<DataToTransfer> {
        val firstTenImagesOnDevice = mutableListOf<DataToTransfer>()

        return firstTenImagesOnDevice
    }

    override suspend fun getMetaDataOfImage(imageUri: Uri): DataToTransfer {

        return mutableListOf<DataToTransfer>().first()
    }
}