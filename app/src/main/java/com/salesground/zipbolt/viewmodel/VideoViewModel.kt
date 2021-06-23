package com.salesground.zipbolt.viewmodel

import androidx.lifecycle.ViewModel
import com.salesground.zipbolt.repository.VideoRepositoryI
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VideoViewModel @Inject constructor(
    private val videoRepositoryI: VideoRepositoryI
) : ViewModel() {


    fun getAllVideosOnDevice(){
        videoRepositoryI.getVideosOnDevice()
    }
}