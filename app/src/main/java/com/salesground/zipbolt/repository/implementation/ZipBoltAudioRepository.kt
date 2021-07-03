package com.salesground.zipbolt.repository.implementation

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.salesground.zipbolt.communication.MediaTransferProtocol
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.repository.AudioRepositoryI
import com.salesground.zipbolt.repository.SavedFilesRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.DataInputStream

class ZipBoltAudioRepository(
    savedFilesRepository: SavedFilesRepository,
    @ApplicationContext val context: Context
) : AudioRepositoryI {

    override suspend fun insertAudioIntoMediaStore(
        audioName: String,
        audioSize: Long,
        audioDuration: Long,
        dataInputStream: DataInputStream,
        transferMetaDataUpdateListener: MediaTransferProtocol.TransferMetaDataUpdateListener,
        dataReceiveListener: MediaTransferProtocol.DataReceiveListener
    ) {

    }

    override suspend fun getAudioOnDevice(): MutableList<DataToTransfer> {
        val deviceAudio = mutableListOf<DataToTransfer>()

        val collection: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY) else
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val audioAlbumCollection: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            MediaStore.Audio.Albums.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY) else
            MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI

        val projection: Array<String> = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.ALBUM_ID
        )
        val sortOrder = "$${MediaStore.Audio.Media.DATE_MODIFIED} DESC"

        context.contentResolver.query(
            collection,
            projection,
            null, null,
            sortOrder
        )?.let { cursor: Cursor ->
            val audioIdColumnIndex = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val audioDisplayNameColumnIndex =
                cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)
            val audioSizeColumnIndex = cursor.getColumnIndex(MediaStore.Audio.Media.SIZE)
            val audioDurationColumnIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)
            val albumIdColumnIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)

            while (cursor.moveToNext()) {
                val audioId = cursor.getLong(audioIdColumnIndex)
                val albumId = cursor.getLong(albumIdColumnIndex)


                deviceAudio.add(
                    DataToTransfer.DeviceAudio(
                        audioUri = ContentUris.withAppendedId(collection, audioId),
                        audioDisplayName = cursor.getString(audioDisplayNameColumnIndex),
                        audioDuration = cursor.getLong(audioDurationColumnIndex),
                        audioSize = cursor.getLong(audioSizeColumnIndex),
                        audioArtPath = ContentUris.withAppendedId(audioAlbumCollection, albumId)
                    )
                )

            }
        }
        return deviceAudio
    }

}