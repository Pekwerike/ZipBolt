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

    private var peeredDevice: WifiP2pDevice = WifiP2pDevice().apply {
        deviceName = "Samsung Galaxy X2"
        deviceAddress = "192.021.294.24"
    }
    private var wifiP2pCurrentConnectionInfo: WifiP2pInfo = WifiP2pInfo()
    private val _peerConnectionUIState =
        MutableLiveData<PeerConnectionUIState>(PeerConnectionUIState.NoConnectionUIAction)
    val peerConnectionUIState: LiveData<PeerConnectionUIState>
        get() = _peerConnectionUIState

    fun peerConnectionNoAction() {
        _peerConnectionUIState.value = PeerConnectionUIState.NoConnectionUIAction
    }

    fun collapsedConnectedToPeerNoAction() {
        _peerConnectionUIState.value = PeerConnectionUIState.CollapsedConnectedToPeerNoAction(
            wifiP2pCurrentConnectionInfo,
            peeredDevice
        )
    }

    fun expandedConnectedToPeerNoAction() {
        _peerConnectionUIState.value = PeerConnectionUIState.ExpandedConnectedToPeerNoAction(
            wifiP2pCurrentConnectionInfo,
            peeredDevice
        )
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


    fun connectedToPeer(wifiP2pInfo: WifiP2pInfo, peeredDevice: WifiP2pDevice) {
        wifiP2pCurrentConnectionInfo = wifiP2pInfo // remove this line, later after extensive tests
        this.peeredDevice = peeredDevice
        _peerConnectionUIState.value =
            PeerConnectionUIState.CollapsedConnectedToPeerNoAction(wifiP2pInfo, peeredDevice)
    }


    fun totalFileReceiveComplete() {
        hasBeenNotifiedAboutReceive = false
    }


    fun addDataFromReceiveToUIState() {
        if (_peerConnectionUIState.value is PeerConnectionUIState.ExpandedConnectedToPeerTransferOngoing
            || _peerConnectionUIState.value is PeerConnectionUIState.CollapsedConnectedToPeerNoAction
            || _peerConnectionUIState.value is PeerConnectionUIState.ExpandedConnectedToPeerNoAction
        ) {
            expandedConnectedToPeerTransferOngoing()
        } else if (_peerConnectionUIState.value is PeerConnectionUIState.CollapsedConnectedToPeerTransferOngoing) {
            collapsedConnectedToPeerTransferOngoing()
        }
    }

}