package com.salesground.zipbolt.communicationprotocol

import android.content.Context
import android.os.ParcelFileDescriptor
import com.salesground.zipbolt.model.MediaModel
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.FileInputStream

class ZipBoltMTP(private val context: Context) {

    suspend fun transferMedia(mediaItems: MutableList<MediaModel>, DOS: DataOutputStream) {
        DOS.writeInt(mediaItems.size)
        mediaItems.forEach { mediaModel: MediaModel ->
            DOS.writeUTF(mediaModel.mediaDisplayName)
            DOS.writeLong(mediaModel.mediaSize)
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

    suspend fun receiveMedia(DIS: DataInputStream){

    }
}