package com.salesground.zipbolt.communicationprotocol

import android.content.Context
import com.salesground.zipbolt.model.MediaModel
import java.io.DataOutputStream

class ZipBoltMediaTransferProtocol(private val context: Context) {

    fun transferMedia(mediaItems : MutableList<MediaModel>, DOS : DataOutputStream){
        DOS.writeInt(mediaItems.size)
        mediaItems.forEach { mediaModel : MediaModel ->
            DOS.writeUTF(mediaModel.mediaDisplayName)
            DOS.writeLong(mediaModel.mediaSize)
            context.
        }
    }
}