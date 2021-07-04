package com.salesground.zipbolt.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.repository.AudioRepositoryI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class AudioViewModel(private val audioRepositoryI: AudioRepositoryI) : ViewModel() {

    private val _deviceAudio = MutableLiveData<MutableList<DataToTransfer>>()
    val deviceAudio: LiveData<MutableList<DataToTransfer>>
        get() = _deviceAudio

    init {
        getDeviceAudio()
    }

    private fun getDeviceAudio() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val deviceAudio = audioRepositoryI.getAudioOnDevice()
                withContext(Dispatchers.Main) {
                    _deviceAudio.value = deviceAudio
                }
            }
        }
    }
}