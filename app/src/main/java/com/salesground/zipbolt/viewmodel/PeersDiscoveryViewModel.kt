package com.salesground.zipbolt.viewmodel

import android.net.wifi.p2p.WifiP2pDevice
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class PeersDiscoveryViewModel() : ViewModel() {
    private var normalDiscoveredPeerSet = mutableSetOf<WifiP2pDevice>()
    private val _discoveredPeerSet = MutableLiveData<MutableSet<WifiP2pDevice>>(null)
    val discoveredPeerSet: LiveData<MutableSet<WifiP2pDevice>>
        get() = _discoveredPeerSet

    fun addDiscoveredDevice(wifiP2pDevice: WifiP2pDevice) {
        normalDiscoveredPeerSet.add(wifiP2pDevice)
        viewModelScope.launch(Dispatchers.Main) {
            _discoveredPeerSet.value = normalDiscoveredPeerSet
        }
    }

    fun clearDiscoveredPeerSet() {
        normalDiscoveredPeerSet = mutableSetOf()
        viewModelScope.launch {
            _discoveredPeerSet.value = normalDiscoveredPeerSet
        }
    }

}