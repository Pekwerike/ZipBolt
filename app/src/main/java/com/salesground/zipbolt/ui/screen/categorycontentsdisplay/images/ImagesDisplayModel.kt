package com.salesground.zipbolt.ui.screen.categorycontentsdisplay.images

import com.salesground.zipbolt.model.MediaModel

sealed class ImagesDisplayModel {
    data class ImagesDateModifiedHeader(val dateModified: String) : ImagesDisplayModel()
    data class DeviceImageDisplay(val deviceImage: MediaModel) : ImagesDisplayModel()
}

