package com.salesground.zipbolt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    init {
        getAllVideosOnDevice()
    }

    private fun getAllVideosOnDevice() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                videoRepositoryI.getVideosOnDevice()
            }
        }
    }
}