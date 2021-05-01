package com.salesground.zipbolt.viewmodel

import android.net.wifi.p2p.WifiP2pDevice
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.salesground.zipbolt.model.ui.PeerConnectionUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor() : ViewModel() {

    private var currentPeersList: MutableList<WifiP2pDevice> = mutableListOf()
    private val _peerConnectionUIState =
        MutableLiveData<PeerConnectionUIState>(PeerConnectionUIState.NoConnectionUIAction)
    val peerConnectionUIState: LiveData<PeerConnectionUIState>
        get() = _peerConnectionUIState

    fun collapsedSearchingForPeers(){
        _peerConnectionUIState.value = PeerConnectionUIState.CollapsedSearchingForPeer(currentPeersList.size)
    }

    fun expandedSearchingForPeers(){
        _peerConnectionUIState.value = PeerConnectionUIState.ExpandedSearchingForPeer(currentPeersList)
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

    fun wifiP2pDiscoveryStopped() {
        _peerConnectionUIState.value = PeerConnectionUIState.NoConnectionUIAction
    }

    fun wifiP2pDiscoveryStarted() {
        _peerConnectionUIState.value =
            PeerConnectionUIState.ExpandedSearchingForPeer(mutableListOf())
    }
}