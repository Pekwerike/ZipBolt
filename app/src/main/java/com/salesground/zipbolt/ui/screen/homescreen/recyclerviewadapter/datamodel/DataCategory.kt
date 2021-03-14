package com.salesground.zipbolt.ui.screen.homescreen.recyclerviewadapter.datamodel

import com.salesground.zipbolt.model.ApplicationModel
import com.salesground.zipbolt.model.MediaModel

sealed class DataCategory {
    abstract val itemId: String

    data class Application(val applicationModel: ApplicationModel) : DataCategory() {
        override val itemId: String
            get() = applicationModel.apkPath
    }

    data class Image(val imageModel: MediaModel) : DataCategory() {
        override val itemId: String
            get() = imageModel.mediaUri.toString()
    }

    data class Video(val videoModel: MediaModel) : DataCategory(){
        override val itemId: String
            get() = videoModel.mediaUri.toString()
    }
    data class Music(val musicModel: MediaModel) : DataCategory(){
        override val itemId: String
            get() = musicModel.mediaUri.toString()
    }
}
