package com.salesground.zipbolt.viewmodel

import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pInfo
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.salesground.zipbolt.model.ui.PeerConnectionUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor() : ViewModel() {
    private var deviceToConnect: WifiP2pDevice = WifiP2pDevice()
    private var peeredDeviceInfo: WifiP2pInfo = WifiP2pInfo()
    private var currentPeersList: MutableList<WifiP2pDevice> = mutableListOf()
    private val _peerConnectionUIState =
        MutableLiveData<PeerConnectionUIState>(PeerConnectionUIState.NoConnectionUIAction)
    val peerConnectionUIState: LiveData<PeerConnectionUIState>
        get() = _peerConnectionUIState

    fun collapsedConnectedToPeerNoAction() {
        _peerConnectionUIState.value = PeerConnectionUIState.CollapsedConnectedToPeerNoAction(peeredDeviceInfo, deviceToConnect)
    }

    fun expandedConnectedToPeerNoAction() {
        _peerConnectionUIState.value = PeerConnectionUIState.ExpandedConnectedToPeerNoAction(peeredDeviceInfo, deviceToConnect)
    }

    fun collapsedSearchingForPeers() {
        _peerConnectionUIState.value =
            PeerConnectionUIState.CollapsedSearchingForPeer(currentPeersList.size)
    }

    fun expandedSearchingForPeers() {
        _peerConnectionUIState.value =
            PeerConnectionUIState.ExpandedSearchingForPeer(currentPeersList)
    }

    fun connectedToPeer(wifiP2pInfo: WifiP2pInfo) {
        peeredDeviceInfo = wifiP2pInfo
        deviceToConnect?.let {
            _peerConnectionUIState.value =
                PeerConnectionUIState.CollapsedConnectedToPeerNoAction(wifiP2pInfo, it)
        }
    }

    fun peersListAvailable(peersList: MutableList<WifiP2pDevice>) {
        currentPeersList = peersList
        _peerConnectionUIState.value = when (_peerConnectionUIState.value) {
            is PeerConnectionUIState.CollapsedSearchingForPeer -> {
                PeerConnectionUIState.CollapsedSearchingForPeer(peersList.size)
            }
            is PeerConnectionUIState.ExpandedSearchingForPeer -> {
                PeerConnectionUIState.ExpandedSearchingForPeer(peersList)
            }
            else -> PeerConnectionUIState.NoConnectionUIAction
        }
    }

    fun updateDeviceToConnect(wifiP2pDevice: WifiP2pDevice) {
        deviceToConnect = wifiP2pDevice
    }

    fun wifiP2pDiscoveryStopped() {
        _peerConnectionUIState.value = PeerConnectionUIState.NoConnectionUIAction
    }

    fun wifiP2pDiscoveryStarted() {
        if (_peerConnectionUIState.value == PeerConnectionUIState.NoConnectionUIAction) {
            _peerConnectionUIState.value =
                PeerConnectionUIState.ExpandedSearchingForPeer(mutableListOf())
        }
    }
}