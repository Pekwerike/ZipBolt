package com.salesground.zipbolt.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.repository.AudioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AudioViewModel @Inject constructor(private val audioRepository: AudioRepository) :
    ViewModel() {

    private val _deviceAudio = MutableLiveData<MutableList<DataToTransfer>>()
    val deviceAudio: LiveData<MutableList<DataToTransfer>>
        get() = _deviceAudio

    fun getDeviceAudio() {
        viewModelScope.launch(Dispatchers.IO) {
            val deviceAudio = audioRepository.getAudioOnDevice()
            withContext(Dispatchers.Main) {
                _deviceAudio.value = deviceAudio
            }
        }
    }

}