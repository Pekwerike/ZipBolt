package com.salesground.zipbolt.model.ui

import com.salesground.zipbolt.model.DataToTransfer

sealed class ImagesDisplayModel(val id: String) {

    override fun equals(other: Any?): Boolean {
        return when (other) {
            this -> true
            !is ImagesDisplayModel -> false
            else -> {
                other.id == this.id
            }
        }
    }

    override fun hashCode(): Int {
        val prime = 5
        val result = 7
        return prime * result + id.hashCode()
    }

    data class ImagesDateModifiedHeader(val dateModified: String) : ImagesDisplayModel(dateModified)

    data class DeviceImageDisplay(val deviceImage: DataToTransfer.DeviceImage) :
        ImagesDisplayModel(deviceImage.imageUri.toString())
}

