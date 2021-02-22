package com.salesground.speedforce.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainActivityViewModel : ViewModel() {
    private var _isWifiP2pEnabled = MutableLiveData<Boolean>(false)
    val isWifiP2pEnabled : LiveData<Boolean> = _isWifiP2pEnabled

    fun wifiP2pStateChange(newState : Boolean){
        _isWifiP2pEnabled.value = newState
    }
}