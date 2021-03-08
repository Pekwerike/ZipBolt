package com.salesground.zipbolt.communicationprotocol

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.util.Log
import com.salesground.zipbolt.model.MediaCategory
import com.salesground.zipbolt.model.MediaModel
import com.salesground.zipbolt.repository.ImageRepository
import com.salesground.zipbolt.repository.ZipBoltSavedFilesRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.*
import java.util.Collections.min
import javax.inject.Inject


import kotlin.math.min

class ZipBoltMTP @Inject constructor (
    @ApplicationContext private val context: Context,
    private val zipBoltSavedFilesRepository: ZipBoltSavedFilesRepository) {

    fun transferMedia(mediaItems: MutableList<MediaModel>, DOS: DataOutputStream) {
        DOS.writeInt(mediaItems.size)
        mediaItems.forEach { mediaModel: MediaModel ->
            DOS.writeUTF(mediaModel.mediaDisplayName)
            DOS.writeLong(mediaModel.mediaSize)
            DOS.writeUTF(mediaModel.mimeType)
            context.contentResolver.openFileDescriptor(mediaModel.mediaUri, "r").apply {
                this?.let { parcelFileDescriptor: ParcelFileDescriptor ->
                    val mediaModelFileInputStream =
                        FileInputStream(parcelFileDescriptor.fileDescriptor)
                    val bufferArray = ByteArray(10_000_000)
                    var lengthRead: Int

                    while (mediaModelFileInputStream.read(bufferArray)
                            .also { lengthRead = it } > 0
                    ) {
                        DOS.write(bufferArray, 0, lengthRead)
                    }
                    mediaModelFileInputStream.close()
                }
            }

        }
    }

     fun receiveMedia(DIS: DataInputStream) {
        val numberOfItemsSent = DIS.readInt()
        // Log.i("NewTransfer", "Received $numberOfItemsSent images")
        for (i in 0 until numberOfItemsSent) {
            val mediaName = DIS.readUTF()
            var mediaSize = DIS.readLong()
            val mediaType = DIS.readUTF()

            // read media bytes and save it into the media store based on the mime type
            when {
                mediaType.contains("image" , true) -> {
                    ImageRepository(context, zipBoltSavedFilesRepository).insertImageIntoMediaStore(mediaName,
                    mediaSize, mediaType, DIS)
                }
                mediaType.contains("video", true) -> {

                }
                mediaType.contains("audio", true) -> {

                }
            }

        }
    }

}