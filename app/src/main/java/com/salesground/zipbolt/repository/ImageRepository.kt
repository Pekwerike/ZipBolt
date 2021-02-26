package com.salesground.zipbolt.repository

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.salesground.zipbolt.model.MediaModel
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class ImageRepository(private val applicationContext: Context) : IImageRepositoryInterface {

    override fun fetchAllImagesOnDevice(): MutableList<MediaModel> {
        val allImagesOnDevice: MutableList<MediaModel> = mutableListOf()
        val collection: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(
                MediaStore.VOLUME_EXTERNAL_PRIMARY
            )
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val projection: Array<String> = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.SIZE
        )

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

            while (moveToNext()) {
                val imageId = getLong(imageIdColumnIndex)
                val imageDateAdded = getLong(imageDateAddedColumnIndex)
                val imageDisplayName = getString(imageDisplayNameColumnIndex)
                val imageSize = getLong(imageSizeColumnIndex)

                val imageUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    imageId
                )


                allImagesOnDevice.add(
                    MediaModel(
                        mediaUri = imageUri,
                        mediaDateAdded = imageDateAdded,
                        mediaDisplayName = imageDisplayName,
                        mediaSize = imageSize
                    )
                )
            }
        }
        return allImagesOnDevice
    }

    override fun convertImageModelToFile(imagesToConvert: MutableList<MediaModel>): MutableList<File> {
        val imageFiles : MutableList<File> = mutableListOf()
        val imageFolder = File(applicationContext.getExternalFilesDir(null), "SpeedForce")
        if(!imageFolder.exists()) imageFolder.mkdirs()

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
}