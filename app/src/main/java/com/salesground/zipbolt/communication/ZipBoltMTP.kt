package com.salesground.zipbolt.communication

import android.content.Context
import android.os.ParcelFileDescriptor
import com.salesground.zipbolt.model.MediaModel
import com.salesground.zipbolt.repository.ZipBoltSavedFilesRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.*
import javax.inject.Inject

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

                }
                mediaType.contains("video", true) -> {

                }
                mediaType.contains("audio", true) -> {

                }
            }

        }
    }

}