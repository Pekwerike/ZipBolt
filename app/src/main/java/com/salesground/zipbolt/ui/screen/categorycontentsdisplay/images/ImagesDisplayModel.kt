package com.salesground.zipbolt.ui.screen.categorycontentsdisplay.images

import com.salesground.zipbolt.model.DataToTransfer

sealed class ImagesDisplayModel {
    abstract val id : String
    data class ImagesDateModifiedHeader(val dateModified: String) : ImagesDisplayModel(){
        override val id: String
            get() = dateModified
    }
    data class DeviceImageDisplay(val deviceImage: DataToTransfer.DeviceImage) : ImagesDisplayModel(){
        override val id: String
            get() = deviceImage.imageUri.toString()
    }
}

