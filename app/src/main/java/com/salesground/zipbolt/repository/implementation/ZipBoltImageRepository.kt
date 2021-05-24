package com.salesground.zipbolt.repository.implementation

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.salesground.zipbolt.communication.MediaTransferProtocol
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.repository.*
import com.salesground.zipbolt.utils.customizeDate
import com.salesground.zipbolt.utils.parseDate
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import java.io.DataInputStream
import java.io.File
import java.util.*
import javax.inject.Inject


open class ZipBoltImageRepository @Inject constructor(
    @ApplicationContext
    private val applicationContext: Context,
) : ImageRepository {


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
            val sortOrder =
                if (limit != 0) "${MediaStore.Images.Media.DATE_MODIFIED} DESC LIMIT $limit"
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
                            imageDateModified = (imageDateModified * 1000).parseDate()
                                .customizeDate(),
                            imageBucketName = imageBucketDisplayName
                        )
                    )
                }
            }
        return deviceImages
    }

    override suspend fun insertImageIntoMediaStore(
        displayName: String,
        size: Long,
        dataInputStream: DataInputStream,
        transferMetaDataUpdateListener: (MediaTransferProtocol.MediaTransferProtocolMetaData) -> Unit,
        bytesReadListener: (imageDisplayName: String, imageSize: Long, percentageOfDataRead: Float, imageUri: Uri?) -> Unit
    ) {

    }


    override suspend fun getMetaDataOfImage(image: DataToTransfer.DeviceImage): DataToTransfer {
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
        val selectionArguments = arrayOf(image.imageId.toString())

        applicationContext.contentResolver.query(
            collection,
            projection,
            selection,
            selectionArguments,
            null
        )?.let { cursor ->
            if (cursor.moveToFirst()) {
                //val imageMimeType =
                  //  cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE))
                val imageSize = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.SIZE))
                val imageDisplayName =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME))

               return image.apply {
                   this.dataDisplayName = imageDisplayName
                 //  this.dataType = imageMimeType
                   this.dataSize = imageSize
                   this.imageMimeType = imageMimeType
                   this.imageSize = imageSize
                   this.imageDisplayName = imageDisplayName
               }
            }
        }
        return image
    }

    protected fun confirmImageName(mediaName: String?): String {
        return if (mediaName != null) {
            if (isImageInMediaStore(mediaName)) {
                "IMG" + Random().nextInt(100000).toString() + mediaName
            } else {
                mediaName
            }
        } else "IMG" + System.currentTimeMillis() + ".jpg"
    }

    private fun isImageInMediaStore(imageName: String): Boolean {
        val collection = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            }
            else -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }
        val projection = arrayOf(MediaStore.Images.Media.DISPLAY_NAME)
        val selection = "${MediaStore.Images.Media.DISPLAY_NAME} == ?"
        val selectionArgs = arrayOf(imageName)
        val selectionOrder = "${MediaStore.Images.Media.DISPLAY_NAME} ASC LIMIT 1"
        applicationContext.contentResolver.query(
            collection,
            projection,
            selection,
            selectionArgs,
            selectionOrder
        )?.let { cursor ->
            val imageDisplayNameColumnIndex =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            if (cursor.moveToFirst()) {
                val retrievedImageDisplayName = cursor.getString(imageDisplayNameColumnIndex)
                if (retrievedImageDisplayName == imageName) return true
            }
        }
        return false
    }

}