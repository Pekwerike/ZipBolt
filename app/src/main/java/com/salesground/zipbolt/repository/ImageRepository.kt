package com.salesground.zipbolt.repository

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.salesground.zipbolt.model.MediaCategory
import com.salesground.zipbolt.model.MediaModel
import com.salesground.zipbolt.repository.repositoryinterface.ImageRepositoryInterface
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


class ImageRepository(private val applicationContext: Context) : ImageRepositoryInterface {

    override fun fetchAllImagesOnDevice(): MutableList<MediaModel> {
        val allImagesOnDevice: MutableList<MediaModel> = mutableListOf()
        val collection: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(
                MediaStore.VOLUME_EXTERNAL_PRIMARY
            )
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val projection: Array<String> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME
            )
        } else {
            arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.DATA
            )
        }

        val selection = null
        val selectionArgs = null
        val sortOrder = "${MediaStore.Images.Media.DATE_MODIFIED} DESC"

        applicationContext.contentResolver.query(
            collection,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.apply {
            val imageIdColumnIndex = getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val imageDateAddedColumnIndex =
                getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
            val imageDisplayNameColumnIndex =
                getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val imageSizeColumnIndex = getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
            val imageMimeTypeColumnIndex = getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE)



            while (moveToNext()) {
                val imageId = getLong(imageIdColumnIndex)
                val imageDateAdded = getLong(imageDateAddedColumnIndex)
                val imageDisplayName = getString(imageDisplayNameColumnIndex)
                val imageSize = getLong(imageSizeColumnIndex)
                val imageMimeType = getString(imageMimeTypeColumnIndex)

                val imageUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    imageId
                )
                val imageParentFolderName = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val imageBucketNameColumnIndex =
                        getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
                    getString(imageBucketNameColumnIndex)
                } else {
                    val imageDataColumnIndex = getColumnIndex(MediaStore.Images.Media.DATA)
                    File(getString(imageDataColumnIndex)).parentFile!!.name
                }


                allImagesOnDevice.add(
                    MediaModel(
                        mediaUri = imageUri,
                        mediaDateAdded = imageDateAdded,
                        mediaDisplayName = imageDisplayName,
                        mediaSize = imageSize,
                        mediaCategory = MediaCategory.IMAGE,
                        mimeType = imageMimeType,
                        mediaBucketName = imageParentFolderName
                    )
                )
            }
        }
        return allImagesOnDevice
    }

    override fun convertImageModelToFile(imagesToConvert: MutableList<MediaModel>): MutableList<File> {
        val imageFiles: MutableList<File> = mutableListOf()
        val imageFolder = File(applicationContext.getExternalFilesDir(null), "SpeedForce")
        if (!imageFolder.exists()) imageFolder.mkdirs()

        imagesToConvert.forEach {

            val imageFile = File(imageFolder, it.mediaDisplayName)
            val imageFileOutputStream = FileOutputStream(imageFile)

            applicationContext.contentResolver.openFileDescriptor(it.mediaUri, "r")?.apply {
                val fileInputStream = FileInputStream(this.fileDescriptor)
                val buffer = ByteArray(1_000_000)
                var length: Int
                while (fileInputStream.read(buffer).also { length = it } != -1) {
                    imageFileOutputStream.write(buffer, 0, length)
                }
                fileInputStream.close()
            }
            imageFileOutputStream.flush()
            imageFileOutputStream.close()
            imageFiles.add(imageFile)
        }
        return imageFiles
    }

    suspend fun insertImageIntoMediaStore(imageFile: File) {
        val contentValues = ContentValues(7).apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, imageFile.name)
            put(MediaStore.Images.Media.MIME_TYPE, "image/*")
            put(MediaStore.Images.Media.BUCKET_DISPLAY_NAME, imageFile.parent)
            put(MediaStore.Images.Media.DATA, imageFile.absolutePath)
            put(MediaStore.Images.Media.SIZE, imageFile.length())
            put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis())
            put(MediaStore.Images.Media.OWNER_PACKAGE_NAME, applicationContext.packageName)
        }

        applicationContext.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
    }
}