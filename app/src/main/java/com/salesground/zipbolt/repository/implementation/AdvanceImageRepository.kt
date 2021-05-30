package com.salesground.zipbolt.repository.implementation

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.salesground.zipbolt.communication.MediaTransferProtocol.*
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.repository.SavedFilesRepository
import com.salesground.zipbolt.repository.ZIP_BOLT_MAIN_DIRECTORY
import com.salesground.zipbolt.repository.ZipBoltMediaCategory
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.*
import javax.inject.Inject
import kotlin.math.min

class AdvanceImageRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val savedFilesRepository: SavedFilesRepository
) : ZipBoltImageRepository(context) {
    private val buffer = ByteArray(1024 * 16)
    private val contentValues = ContentValues()


    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun insertImageIntoMediaStore(
        displayName: String,
        size: Long,
        dataInputStream: DataInputStream,
        transferMetaDataUpdateListener: (MediaTransferProtocolMetaData) -> Unit,
        bytesReadListener:
            (
            imageDisplayName: String,
            imageSize: Long,
            percentageOfDataRead: Float,
            imageUri: Uri?,
            dataTransferStatus: DataToTransfer.TransferStatus
        ) -> Unit
    ) {
        var mediaSize = size
        val verifiedImageName = confirmImageName(displayName)
        val imagesBaseDirectory =
            savedFilesRepository.getZipBoltMediaCategoryBaseDirectory(ZipBoltMediaCategory.IMAGES_BASE_DIRECTORY)
        val imageFile = File(imagesBaseDirectory, verifiedImageName)
        val imageFileBufferedOutputStream = BufferedOutputStream(FileOutputStream(imageFile))

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

        // percentage of bytes read is 0% here
        bytesReadListener(
            displayName,
            size,
            0f,
            null,
            DataToTransfer.TransferStatus.RECEIVE_STARTED
        )


        while (mediaSize > 0) {
            // read the current transfer status, to determine whether to continue with the transfer
            when (dataInputStream.readInt()) {
                MediaTransferProtocolMetaData.KEEP_RECEIVING.value -> {
                }
                MediaTransferProtocolMetaData.CANCEL_ACTIVE_RECEIVE.value -> {
                    // delete image file
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
            mediaSize -= min(buffer.size.toLong(), mediaSize).toInt()

            bytesReadListener(
                displayName,
                size,
                ((size - mediaSize) / size.toFloat()) * 100f,
                null,
                DataToTransfer.TransferStatus.RECEIVE_ONGOING
            )
        }

        imageFileBufferedOutputStream.close()

        // insert image into the media store
        context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )?.let { imageUri ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                context.contentResolver.update(imageUri, contentValues, null, null)
            }

            // percentage of image read is 100% with the image uri
            bytesReadListener(
                displayName,
                size,
                100f,
                imageUri,
                DataToTransfer.TransferStatus.RECEIVE_COMPLETE
            )
        }
    }
}
