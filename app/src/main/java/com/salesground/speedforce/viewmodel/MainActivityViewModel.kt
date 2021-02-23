package com.salesground.speedforce.viewmodel

import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pInfo
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainActivityViewModel : ViewModel() {
    private var _isWifiP2pEnabled = MutableLiveData(false)
    val isWifiP2pEnabled: LiveData<Boolean> = _isWifiP2pEnabled

    private var _discoveredPeersList = MutableLiveData(mutableListOf<WifiP2pDevice>())
    val discoveredPeersList: LiveData<MutableList<WifiP2pDevice>> = _discoveredPeersList


    private var _peeredDeviceConnectionInfo = MutableLiveData<WifiP2pInfo>()
    val peeredDeviceConnectionInfo: LiveData<WifiP2pInfo> = _peeredDeviceConnectionInfo

    var discoveredPeersListState = mutableStateOf(mutableListOf<WifiP2pDevice>())
        private set
    var isWifiP2pEnabledState = mutableStateOf(false)
        private set

    var peeredDeviceConnectionInfoState = mutableStateOf<WifiP2pInfo>(WifiP2pInfo())
        private set

    fun wifiP2pStateChange(newState: Boolean) {
        _isWifiP2pEnabled.value = newState
        isWifiP2pEnabledState.value = newState
    }

    fun discoveredPeersListChanged(newDiscoveredPeersList: MutableList<WifiP2pDevice>) {
        _discoveredPeersList.value = newDiscoveredPeersList
        discoveredPeersListState.value = newDiscoveredPeersList
    }

    fun peeredDeviceConnectionInfoUpdated(connectionInfo: WifiP2pInfo) {
        _peeredDeviceConnectionInfo.value = connectionInfo
        peeredDeviceConnectionInfoState.value = connectionInfo
    }
}