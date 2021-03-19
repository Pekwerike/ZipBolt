package com.salesground.zipbolt.repository.implementation

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.net.toUri
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.repository.ImageRepository
import com.salesground.zipbolt.utils.parseDate
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

    override suspend fun getImagesOnDevice(limit: Int): MutableList<DataToTransfer> {
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
        val sortOrder = if (limit != 0) "${MediaStore.Images.Media.DATE_MODIFIED} DESC LIMIT $limit"
        else "${MediaStore.Images.Media.DATE_MODIFIED} DESC"

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
                        imageId = imageId,
                        imageUri = ContentUris.withAppendedId(collection, imageId),
                        imageDateModified = imageDateModified.parseDate(),
                        imageBucketName = imageBucketDisplayName
                    )
                )
            }
        }
        return deviceImages
    }


    override suspend fun getMetaDataOfImage(image: DataToTransfer): DataToTransfer {
        val imageToExtractData = image as DataToTransfer.DeviceImage
        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }
        val projection = arrayOf(
            MediaStore.Images.Media.MIME_TYPE,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.DISPLAY_NAME
        )
        val selection = "${MediaStore.Images.Media._ID} =? "
        val selectionArguments = arrayOf(imageToExtractData.imageId.toString())

        applicationContext.contentResolver.query(
            collection,
            projection,
            selection,
            selectionArguments,
            null
        )?.let { cursor ->
            if (cursor.moveToFirst()) {
                val imageMimeType =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE))
                val imageSize = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.SIZE))
                val imageDisplayName =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME))

                return DataToTransfer.DeviceImage(
                    imageId = imageToExtractData.imageId,
                    imageUri = imageToExtractData.imageUri,
                    imageDateModified = imageToExtractData.imageDateModified,
                    imageDisplayName = imageDisplayName,
                    imageBucketName = imageToExtractData.imageBucketName,
                    imageMimeType = imageMimeType,
                    imageSize = imageSize
                )

            }
        }
        return imageToExtractData
    }
}