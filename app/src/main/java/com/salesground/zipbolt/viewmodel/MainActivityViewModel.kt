package com.salesground.zipbolt.viewmodel

import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pInfo
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.model.ui.PeerConnectionUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor() : ViewModel() {

    private var hasBeenNotifiedAboutReceive: Boolean = false

    private var wifiP2pCurrentConnectionInfo: WifiP2pInfo = WifiP2pInfo()
    private val _peerConnectionUIState =
        MutableLiveData<PeerConnectionUIState>(PeerConnectionUIState.NoConnectionUIAction)
    val peerConnectionUIState: LiveData<PeerConnectionUIState>
        get() = _peerConnectionUIState

    fun peerConnectionNoAction() {
        _peerConnectionUIState.value = PeerConnectionUIState.NoConnectionUIAction
    }

    fun collapsedConnectedToPeerTransferOngoing() {
        _peerConnectionUIState.value =
            PeerConnectionUIState.CollapsedConnectedToPeerTransferOngoing(
                wifiP2pCurrentConnectionInfo
            )
    }


    fun expandedConnectedToPeerReceiveOngoing() {
        if (!hasBeenNotifiedAboutReceive) {
            expandedConnectedToPeerTransferOngoing()
            hasBeenNotifiedAboutReceive = true
        }
    }

    fun expandedConnectedToPeerTransferOngoing() {
        _peerConnectionUIState.value =
            PeerConnectionUIState.ExpandedConnectedToPeerTransferOngoing(
                wifiP2pCurrentConnectionInfo
            )
    }

    fun totalFileReceiveComplete() {
        hasBeenNotifiedAboutReceive = false
    }

}