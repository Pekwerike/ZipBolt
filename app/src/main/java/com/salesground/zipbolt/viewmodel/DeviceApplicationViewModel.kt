package com.salesground.zipbolt.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.salesground.zipbolt.model.ApplicationModel
import com.salesground.zipbolt.repository.DeviceApplicationsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.FlowCollector
import javax.inject.Inject

@HiltViewModel
class DeviceApplicationViewModel @Inject constructor(
    private val deviceApplicationsRepository : DeviceApplicationsRepository
): ViewModel() {

    private var _allApplicationsOnDevice = MutableLiveData<List<ApplicationModel>>()
    val allApplicationsOnDevice : LiveData<List<ApplicationModel>> = _allApplicationsOnDevice

    fun getAllApplicationsOnDevice() {
        _allApplicationsOnDevice.value = deviceApplicationsRepository.getAllDeviceApplication()
    }
}