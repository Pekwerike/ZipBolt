package com.salesground.zipbolt.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salesground.zipbolt.model.ApplicationModel
import com.salesground.zipbolt.repository.DeviceApplicationsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeviceApplicationViewModel @Inject constructor(
    private val deviceApplicationsRepository : DeviceApplicationsRepository
): ViewModel() {

    private var _allApplicationsOnDevice = MutableLiveData<List<ApplicationModel>>()
    val allApplicationsOnDevice : LiveData<List<ApplicationModel>> = _allApplicationsOnDevice

    init{
        viewModelScope.launch {
          //  getAllApplicationsOnDevice()
        }
    }

    private suspend fun getAllApplicationsOnDevice() {
        _allApplicationsOnDevice.value =  viewModelScope.async(Dispatchers.IO) {
            deviceApplicationsRepository.getNonSystemAppsOnDevice()
        }.await()

    }
}