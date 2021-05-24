package com.salesground.zipbolt.repository.implementation

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import com.salesground.zipbolt.communication.DataTransferUtils
import com.salesground.zipbolt.communication.MediaTransferProtocol
import com.salesground.zipbolt.communication.MediaTransferProtocol.*
import com.salesground.zipbolt.repository.SavedFilesRepository
import com.salesground.zipbolt.repository.ZIP_BOLT_MAIN_DIRECTORY
import com.salesground.zipbolt.repository.ZipBoltMediaCategory
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.*
import javax.inject.Inject
import kotlin.math.min
import kotlin.math.roundToInt

class AdvanceImageRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val savedFilesRepository: SavedFilesRepository
) : ZipBoltImageRepository(context) {
    private val buffer = ByteArray(1024 * 8)
    private val contentValues = ContentValues()


    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun insertImageIntoMediaStore(
        displayName: String,
        size: Long,
        dataInputStream: DataInputStream,
        transferMetaDataUpdateListener: (MediaTransferProtocolMetaData) -> Unit,
        bytesReadListener:
            (imageDisplayName: String, imageSize: Long, percentageOfDataRead: Float, imageUri: Uri) -> Unit
    ) {
        var mediaSize = size
        val verifiedImageName = confirmImageName(displayName)
        val imagesBaseDirectory =
            savedFilesRepository.getZipBoltMediaCategoryBaseDirectory(ZipBoltMediaCategory.IMAGES_BASE_DIRECTORY)
        val imageFile = File(imagesBaseDirectory, verifiedImageName)

        contentValues.clear()
        contentValues.apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, imageFile.name)
            put(MediaStore.Images.Media.TITLE, imageFile.name)
            put(MediaStore.Images.Media.SIZE, size)
            put(MediaStore.Images.Media.DATA, imageFile.absolutePath)
            put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
            put(MediaStore.Images.Media.DATE_MODIFIED, System.currentTimeMillis() / 1000)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.OWNER_PACKAGE_NAME, context.packageName)
                put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis() / 1000)
                put(MediaStore.Images.Media.IS_PENDING, 1)
                put(
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                    ZIP_BOLT_MAIN_DIRECTORY
                )
            }
        }


        val imageFileBufferedOutputStream = BufferedOutputStream(FileOutputStream(imageFile))


        // percentage of bytes read is 0% here
        /* bytesReadListener(
             displayName,
             size,
             0f,
             imageUri!!
         )*/


        while (mediaSize > 0) {
            // read the current transfer status, to determine whether to continue with the transfer
            when (dataInputStream.readInt()) {
                MediaTransferProtocolMetaData.KEEP_RECEIVING.value -> {
                }
                MediaTransferProtocolMetaData.CANCEL_ACTIVE_RECEIVE.value -> {
                    // delete image file
                    /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                         contentValues.clear()
                         contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                         context.contentResolver.update(
                             imageUri,
                             contentValues,
                             null,
                             null
                         )
                     }*/
                    //   context.contentResolver.delete(imageUri, null, null)
                    imageFile.delete()
                    return
                }
                MediaTransferProtocolMetaData.KEEP_RECEIVING_BUT_CANCEL_ACTIVE_TRANSFER.value -> {
                    transferMetaDataUpdateListener(MediaTransferProtocolMetaData.KEEP_RECEIVING_BUT_CANCEL_ACTIVE_TRANSFER)
                }
            }

            dataInputStream.readFully(
                buffer, 0,
                min(buffer.size.toLong(), mediaSize).toInt()
            )
            imageFileBufferedOutputStream.write(
                buffer,
                0, min(buffer.size.toLong(), mediaSize).toInt()
            )
            imageFileBufferedOutputStream.flush()
            mediaSize -= min(buffer.size.toLong(), mediaSize).toInt()
        }

        imageFileBufferedOutputStream.close()
        context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
        /* val firstBytesReadSize = dataInputStream.read(
             buffer,
             0, size.toInt()
         )

         var readOffset = firstBytesReadSize

         if (firstBytesReadSize != size.toInt()) {
             while (readOffset != size.toInt()) {
                 val ithRead = dataInputStream.read(buffer,
                 readOffset, size.toInt() - readOffset
                 )
                 readOffset += ithRead
             }
         }

         imageFileBufferedOutputStream.write(
             buffer,
             0, size.toInt()
         )

         imageFileBufferedOutputStream.close() */

        /* while (mediaSize > 0) {
             // read the current transfer status, to determine whether to continue with the transfer
             when (dataInputStream.readInt()) {
                 MediaTransferProtocolMetaData.KEEP_RECEIVING.value -> {
                 }
                 MediaTransferProtocolMetaData.CANCEL_ACTIVE_RECEIVE.value -> {
                     // delete image file
                     if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                         contentValues.clear()
                         contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                         context.contentResolver.update(
                             imageUri,
                             contentValues,
                             null,
                             null
                         )
                     }
                     context.contentResolver.delete(imageUri, null, null)
                     imageFile.delete()
                     return
                 }
                 MediaTransferProtocolMetaData.KEEP_RECEIVING_BUT_CANCEL_ACTIVE_TRANSFER.value -> {
                     transferMetaDataUpdateListener(MediaTransferProtocolMetaData.KEEP_RECEIVING_BUT_CANCEL_ACTIVE_TRANSFER)
                 }
             }

             // read the bytes transferred
             val bytesReadLength = dataInputStream.read(
                 buffer,
                 0, min(buffer.size.toLong(), mediaSize).toInt()
             )

             if (bytesReadLength == -1) break
             imageFileBufferedOutputStream.write(buffer, 0, bytesReadLength)
             mediaSize -= bytesReadLength
             // delay(500)

             //  imageOutputStream.write(buffer, 0, bytesReadLength)
             // imageOutputStream.flush()
             bytesReadListener(
                 displayName,
                 size,
                 ((size - mediaSize) / size.toFloat()) * 100f,
                 imageUri
             )
         }
         //  imageFileBufferedOutputStream.close()
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
             contentValues.clear()
             contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
             context.contentResolver.update(imageUri, contentValues, null, null)
         }*/
    }


}
