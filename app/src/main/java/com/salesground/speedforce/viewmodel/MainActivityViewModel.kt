package com.salesground.speedforce.viewmodel

import android.net.wifi.p2p.WifiP2pDevice
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainActivityViewModel : ViewModel() {
    private var _isWifiP2pEnabled = MutableLiveData(false)
    val isWifiP2pEnabled : LiveData<Boolean> = _isWifiP2pEnabled

    private var _discoveredPeersList = MutableLiveData(mutableListOf<WifiP2pDevice>())
    val discoveredPeersList : LiveData<MutableList<WifiP2pDevice>> = _discoveredPeersList

    fun wifiP2pStateChange(newState : Boolean){
        _isWifiP2pEnabled.value = newState
    }

    fun discoveredPeersListChanged(newDiscoveredPeersList : MutableList<WifiP2pDevice>){
        _discoveredPeersList.value = newDiscoveredPeersList
    }
}