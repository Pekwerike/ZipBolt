package com.salesground.zipbolt.repository.implementation

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.repository.*
import com.salesground.zipbolt.utils.customizeDate
import com.salesground.zipbolt.utils.parseDate
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import java.io.DataInputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*
import javax.inject.Inject
import kotlin.math.min


class ZipBoltImageRepository @Inject constructor(
    @ApplicationContext
    private val applicationContext: Context,
    private val savedFilesRepository: SavedFilesRepository
) : ImageRepository {

    private var imageByteReadListener: ImageRepository.ImageByteReadListener? = null


    override fun setImageByteReadListener(byteReadListener: ImageRepository.ImageByteReadListener) {
        imageByteReadListener = byteReadListener
    }

    override suspend fun insertImageIntoMediaStore(
        displayName: String,
        size: Long,
        mimeType: String,
        dataInputStream: DataInputStream
    ) {
        withContext(Dispatchers.IO) {
            var mediaSize = size
            // check if an image with this name is already in the mediaStore
            val verifiedImageName = confirmImageName(displayName)
            val imagesBaseDirectory = savedFilesRepository
                .getZipBoltMediaCategoryBaseDirectory(ZipBoltMediaCategory.IMAGES_BASE_DIRECTORY)
            val imageFile = File(imagesBaseDirectory, verifiedImageName)

            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, imageFile.name)
                put(MediaStore.Images.Media.TITLE, imageFile.name)
                put(MediaStore.Images.Media.SIZE, size)
                put(MediaStore.Images.Media.MIME_TYPE, mimeType)
                put(MediaStore.Images.Media.DATA, imageFile.absolutePath)
                put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis())
                put(MediaStore.Images.Media.DATE_MODIFIED, System.currentTimeMillis())
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Images.Media.OWNER_PACKAGE_NAME, applicationContext.packageName)
                    put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                    put(
                        MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                        ZIP_BOLT_MAIN_DIRECTORY
                    )
                }
            }

            val imageUri = applicationContext.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )

            imageUri?.let {
                applicationContext.contentResolver.openFileDescriptor(imageUri, "w")?.let {
                    val imageFileDataOutputStream = FileOutputStream(it.fileDescriptor)
                    val bufferArray = ByteArray(10_000_000)

                    // percentage of bytes read is 0% here
                    imageByteReadListener?.percentageOfBytesRead(
                        bytesReadPercent = ((size - mediaSize) / size) * 100f
                    )
                    while (mediaSize > 0) {
                        val bytesRead = dataInputStream.read(
                            bufferArray,
                            0,
                            min(mediaSize.toInt(), bufferArray.size)
                        )
                        if (bytesRead == -1) break
                        imageFileDataOutputStream.write(bufferArray, 0, bytesRead)
                        mediaSize -= bytesRead
                        imageByteReadListener?.percentageOfBytesRead(
                            bytesReadPercent = ((size - mediaSize) / size) * 100f
                        )
                    }
                    imageFileDataOutputStream.flush()
                    imageFileDataOutputStream.close()
                    it.close()
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentValues.clear()
                    contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                    applicationContext.contentResolver.update(imageUri, contentValues, null, null)
                }
            }
        }
    }

    override suspend fun getImagesOnDevice(limit: Int): MutableList<DataToTransfer> {
        val deviceImages = mutableListOf<DataToTransfer>()
        withContext(Dispatchers.IO) {
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

    private fun confirmImageName(mediaName: String?): String {
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