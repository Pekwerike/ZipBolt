package com.salesground.zipbolt.communicationprotocol

import android.content.ContentValues
import android.content.Context
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import com.salesground.zipbolt.model.MediaCategory
import com.salesground.zipbolt.model.MediaModel
import java.io.*

class ZipBoltMTP(private val context: Context) {

    suspend fun transferMedia(mediaItems: MutableList<MediaModel>, DOS: DataOutputStream) {
        DOS.writeInt(mediaItems.size)
        mediaItems.forEach { mediaModel: MediaModel ->
            DOS.writeUTF(mediaModel.mediaDisplayName)
            DOS.writeLong(mediaModel.mediaSize)
            DOS.writeUTF(
                when (mediaModel.mediaCategory) {
                    MediaCategory.AUDIO -> MediaCategory.AUDIO.name
                    MediaCategory.VIDEO -> MediaCategory.VIDEO.name
                    else -> MediaCategory.IMAGE.name
                }
            )
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

    suspend fun receiveMedia(DIS: DataInputStream) {
        val numberOfItemsSent = DIS.readInt()
        for (i in 0 until numberOfItemsSent) {
            val mediaName = DIS.readUTF()
            var mediaSize = DIS.readLong()
            val mediaType = DIS.readUTF()

            // read media bytes and save it into the media store based on the mime type
            when (mediaType) {
                MediaCategory.VIDEO.name -> TODO()
                MediaCategory.IMAGE.name -> {
                    val parentRelativePath =
                        File(Environment.getExternalStorageDirectory(), "ZipBolt")
                    if (!parentRelativePath.exists()) parentRelativePath.mkdirs()
                    val imagesRelativePath = File(parentRelativePath, "images")
                    if (!imagesRelativePath.exists()) imagesRelativePath.mkdirs()
                    val imageFile = File(imagesRelativePath, mediaName)

                    val contentValues = ContentValues().apply {
                        put(MediaStore.Images.Media.DISPLAY_NAME, mediaName)
                        put(MediaStore.Images.Media.OWNER_PACKAGE_NAME, context.packageName)
                        put(MediaStore.Images.Media.RELATIVE_PATH, imagesRelativePath.absolutePath)
                        put(MediaStore.Images.Media.DATA, imageFile.absolutePath)
                        put(MediaStore.Images.Media.SIZE, mediaSize)
                        put(MediaStore.Images.Media.MIME_TYPE, "image/*")
                        put(
                            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                            imagesRelativePath.absolutePath
                        )
                        put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis())
                        put(MediaStore.Images.Media.IS_PENDING, 1)
                    }

                    val imageUri = context.contentResolver.insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        contentValues
                    )

                    imageUri?.let {
                        context.contentResolver.openFileDescriptor(imageUri, "w").apply {
                            this?.let {
                                val imageFileDataOutputStream = FileOutputStream(it.fileDescriptor)
                                val bufferArray = ByteArray(10_000_000)

                                while(mediaSize > 0){

                                }
                            }
                        }
                    }

                }
                MediaCategory.AUDIO.name -> TODO()
            }

        }
    }
}