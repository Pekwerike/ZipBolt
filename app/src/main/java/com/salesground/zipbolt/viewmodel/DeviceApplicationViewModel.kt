package com.salesground.zipbolt.viewmodel

import androidx.lifecycle.ViewModel
import com.salesground.zipbolt.repository.DeviceApplicationsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DeviceApplicationViewModel @Inject constructor(
    private val deviceDeviceApplicationsRepository : DeviceApplicationsRepository
): ViewModel() {

}