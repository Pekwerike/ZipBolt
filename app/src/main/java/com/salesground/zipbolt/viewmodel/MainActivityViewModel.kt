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

    private val _peerConnectionUIState =
        MutableLiveData<PeerConnectionUIState>(PeerConnectionUIState.NoConnectionUIAction)
    val peerConnectionUIState: LiveData<PeerConnectionUIState>
        get() = _peerConnectionUIState

    fun peersListAvailable(peersList: MutableList<WifiP2pDevice>) {
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

    fun wifiP2pDiscoveryStoped() {
        _peerConnectionUIState.value = PeerConnectionUIState.NoConnectionUIAction
    }

    fun wifiP2pDiscoveryStarted() {
        _peerConnectionUIState.value =
            PeerConnectionUIState.ExpandedSearchingForPeer(mutableListOf())
    }
}