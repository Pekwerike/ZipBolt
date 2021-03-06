package com.salesground.zipbolt.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.repository.ApplicationsRepositoryInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ApplicationsViewModel @Inject constructor(
    private val applicationsRepositoryInterface: ApplicationsRepositoryInterface
) : ViewModel() {

    private var allNonSystemAppsOnDevice = listOf<DataToTransfer>()
    private val _allApplicationsOnDevice = MutableLiveData<List<DataToTransfer>>(
        listOf()
    )
    val allApplicationsOnDevice: LiveData<List<DataToTransfer>>
        get() = _allApplicationsOnDevice



    fun getAllApplicationsOnDevice() {
        viewModelScope.launch(Dispatchers.IO) {
            allNonSystemAppsOnDevice = applicationsRepositoryInterface.getNonSystemAppsOnDevice()
            withContext(Dispatchers.Main) {
                _allApplicationsOnDevice.value = allNonSystemAppsOnDevice
            }
        }
    }

}