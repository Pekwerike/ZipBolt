package com.salesground.zipbolt.viewmodel

import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pInfo
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.salesground.zipbolt.foregroundservice.ClientService
import com.salesground.zipbolt.foregroundservice.ServerService

class MainActivityViewModel : ViewModel() {
    private var _isWifiP2pEnabled = MutableLiveData(false)
    val isWifiP2pEnabled: LiveData<Boolean> = _isWifiP2pEnabled


    private var _peeredDeviceConnectionInfo = MutableLiveData<WifiP2pInfo?>(null)
    val peeredDeviceConnectionInfo: LiveData<WifiP2pInfo?> = _peeredDeviceConnectionInfo

    var discoveredPeersListState = mutableStateOf(mutableListOf<WifiP2pDevice>())
        private set
    var isWifiP2pEnabledState = mutableStateOf(false)
        private set

    var peeredDeviceConnectionInfoState = mutableStateOf<WifiP2pInfo>(WifiP2pInfo())
        private set

    private var _clientService = MutableLiveData<ClientService>()
    val clientService : LiveData<ClientService> = _clientService

    private var _serverService = MutableLiveData<ServerService>()
    val serverService : LiveData<ServerService> = _serverService

    fun serverServiceReady(serverService: ServerService) {
        _serverService.value = serverService
    }

    fun clientServiceRead(clientService: ClientService) {
        _clientService.value = clientService
    }

    fun wifiP2pStateChange(newState: Boolean) {
        _isWifiP2pEnabled.value = newState
        isWifiP2pEnabledState.value = newState
    }

    fun discoveredPeersListChanged(newDiscoveredPeersList: MutableList<WifiP2pDevice>) {
        discoveredPeersListState.value = newDiscoveredPeersList
    }

    fun peeredDeviceConnectionInfoUpdated(connectionInfo: WifiP2pInfo) {
        _peeredDeviceConnectionInfo.value = connectionInfo
        peeredDeviceConnectionInfoState.value = connectionInfo
    }


}