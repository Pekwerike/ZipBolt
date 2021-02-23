package com.salesground.speedforce.repository

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.salesground.speedforce.model.ImageModel
import java.io.File

class ImageRepository(private val applicationContext: Context) {

    fun fetchAllImagesOnDevice(): MutableList<ImageModel> {
        val allImagesOnDevice : MutableList<ImageModel> = mutableListOf()
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

               allImagesOnDevice.add(ImageModel(
                    imageUri = imageUri,
                    imageDateAdded = imageDateAdded,
                    imageDisplayName = imageDisplayName,
                    imageSize = imageSize
                )
               )
            }
        }
        return allImagesOnDevice
    }

    fun convertImageModelToFile(imagesToConvert : MutableList<ImageModel>) : MutableList<File> =
        imagesToConvert.map { File(it.imageUri.path!!) }.toMutableList()

}