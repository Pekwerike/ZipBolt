package com.salesground.zipbolt.ui.screen.categorycontentsdisplay.images.dto

import com.salesground.zipbolt.model.DataToTransfer

sealed class ImagesDisplayModel(val id: String) {

    data class ImagesDateModifiedHeader(val dateModified: String) : ImagesDisplayModel(dateModified)

    data class DeviceImageDisplay(val deviceImage: DataToTransfer.DeviceImage) :
        ImagesDisplayModel(deviceImage.imageUri.toString())
}

