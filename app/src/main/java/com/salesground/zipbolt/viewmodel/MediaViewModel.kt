package com.salesground.zipbolt.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salesground.zipbolt.model.MediaModel
import com.salesground.zipbolt.repository.ImageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MediaViewModel(application: Application) : AndroidViewModel(application) {
    private val imageRepository = ImageRepository(application)
    var allImagesOnDevice
    = mutableStateListOf<MediaModel>()
        private set

    fun addImages() {
        viewModelScope.launch(Dispatchers.IO) {
            imageRepository.fetchAllImagesOnDevice().collect {
                allImagesOnDevice.add(it)
            }
        }
    }
}