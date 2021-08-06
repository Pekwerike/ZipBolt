package com.salesground.zipbolt.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.repository.VideoRepositoryI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class VideoViewModel @Inject constructor(
    private val videoRepositoryI: VideoRepositoryI
) : ViewModel() {

    private val _allVideosOnDevice = MutableLiveData<MutableList<DataToTransfer>>(mutableListOf())
    val allVideosOnDevice: LiveData<MutableList<DataToTransfer>>
        get() = _allVideosOnDevice

    init {
        getAllVideosOnDevice()
    }

    private fun getAllVideosOnDevice() {
        viewModelScope.launch(Dispatchers.IO) {
            val videosOnDevice = videoRepositoryI.getVideosOnDevice()
            withContext(Dispatchers.Main) {
                _allVideosOnDevice.value = videosOnDevice
            }
        }
    }
}