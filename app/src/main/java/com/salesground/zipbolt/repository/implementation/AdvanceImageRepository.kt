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
import java.io.DataInputStream
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import kotlin.math.min

class AdvanceImageRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val savedFilesRepository: SavedFilesRepository
) : ZipBoltImageRepository(context) {

    @Suppress("BlockingMethodInNonBlockingContext")
    @Synchronized
    override suspend fun insertImageIntoMediaStore(
        displayName: String,
        size: Long,
        mimeType: String,
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

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, imageFile.name)
            put(MediaStore.Images.Media.TITLE, imageFile.name)
            put(MediaStore.Images.Media.SIZE, size)
            put(MediaStore.Images.Media.MIME_TYPE, mimeType)
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

        val imageUri = context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )

        imageUri?.let {
            context.contentResolver.openFileDescriptor(imageUri, "w")
                ?.let { parcelFileDescriptor ->
                    val imageOutputStream =
                        FileOutputStream(parcelFileDescriptor.fileDescriptor)
                    val buffer = ByteArray(10_000_000)

                    // percentage of bytes read is 0% here
                    bytesReadListener(
                        displayName,
                        size,
                        0f,
                        imageUri
                    )

                    while (mediaSize > 0) {
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

                        val bytesRead = dataInputStream.read(
                            buffer,
                            0,
                            min(mediaSize.toInt(), buffer.size)
                        )
                      //  Log.i("TransferMessage", "Read out some bytes")
                        if (bytesRead == -1) break
                        imageOutputStream.write(buffer, 0, bytesRead)
                        mediaSize -= bytesRead
                        bytesReadListener(
                            displayName,
                            size,
                            ((size - mediaSize) / size.toFloat()) * 100f,
                            imageUri
                        )
                    }
                    imageOutputStream.flush()
                    imageOutputStream.close()
                    parcelFileDescriptor.close()
                }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                context.contentResolver.update(imageUri, contentValues, null, null)
            }
        }
    }

}
