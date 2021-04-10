package com.salesground.zipbolt.viewmodel

import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pInfo
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.salesground.zipbolt.foregroundservice.ClientService
import com.salesground.zipbolt.foregroundservice.ServerService
import com.salesground.zipbolt.model.ui.PeerConnectionState

class MainActivityViewModel : ViewModel() {
    private var _peerConnectionState =
        MutableLiveData<PeerConnectionState>(PeerConnectionState.NoConnectionAction)
    val peerConnectionState: LiveData<PeerConnectionState> = _peerConnectionState


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

    private var _clientService = MutableLiveData<ClientService>(null)
    val clientService: LiveData<ClientService> = _clientService

    private var _serverService = MutableLiveData<ServerService>(null)
    val serverService: LiveData<ServerService> = _serverService

    fun updatePeerConnectionState(peerConnectionState: PeerConnectionState) {
        when(peerConnectionState){
            is PeerConnectionState.CollapsedConnectedToPeer -> TODO()
            is PeerConnectionState.CollapsedSearchingForPeer -> {
                _peerConnectionState.value = PeerConnectionState.CollapsedSearchingForPeer(
                    numberOfDevicesFound = discoveredPeersListState.value.size
                )
            }
            is PeerConnectionState.ExpandedConnectedToPeer -> TODO()
            is PeerConnectionState.ExpandedSearchingForPeer -> {
                _peerConnectionState.value = PeerConnectionState.ExpandedSearchingForPeer(
                    devices = discoveredPeersListState.value
                )
            }
            PeerConnectionState.NoConnectionAction -> TODO()
        }

    }

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

        when (_peerConnectionState.value) {
            is PeerConnectionState.CollapsedSearchingForPeer -> {
                _peerConnectionState.value = PeerConnectionState.CollapsedSearchingForPeer(
                    numberOfDevicesFound =
                    newDiscoveredPeersList.size
                )
            }
            is PeerConnectionState.ExpandedSearchingForPeer -> {
                _peerConnectionState.value = PeerConnectionState.ExpandedSearchingForPeer(
                    devices = newDiscoveredPeersList
                )
            }
        }
    }

    fun peeredDeviceConnectionInfoUpdated(connectionInfo: WifiP2pInfo) {
        _peeredDeviceConnectionInfo.value = connectionInfo
        peeredDeviceConnectionInfoState.value = connectionInfo

        when (_peerConnectionState.value) {
            is PeerConnectionState.CollapsedConnectedToPeer -> {
                _peerConnectionState.value = PeerConnectionState.CollapsedConnectedToPeer(
                    peeredDeviceConnectionInfo = connectionInfo
                )
            }
            is PeerConnectionState.ExpandedConnectedToPeer -> {
                _peerConnectionState.value = PeerConnectionState.ExpandedConnectedToPeer(
                    peeredDeviceConnectionInfo = connectionInfo
                )
            }
            is PeerConnectionState.ExpandedSearchingForPeer -> {
                _peerConnectionState.value = PeerConnectionState.ExpandedConnectedToPeer(
                    peeredDeviceConnectionInfo = connectionInfo
                )
            }
            else -> {
                _peerConnectionState.value = PeerConnectionState.CollapsedConnectedToPeer(
                    peeredDeviceConnectionInfo = connectionInfo
                )
            }
        }
    }

}