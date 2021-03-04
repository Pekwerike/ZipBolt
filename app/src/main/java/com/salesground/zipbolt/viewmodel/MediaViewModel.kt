package com.salesground.zipbolt.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.salesground.zipbolt.model.MediaModel
import com.salesground.zipbolt.repository.ImageRepository
import kotlinx.coroutines.flow.MutableStateFlow

class MediaViewModel(application: Application) : AndroidViewModel(application) {
    private val imageRepository = ImageRepository(application)
    var allImagesOnDevice = mutableStateListOf<MediaModel>()
            private set

    fun addImages(){

    }
}