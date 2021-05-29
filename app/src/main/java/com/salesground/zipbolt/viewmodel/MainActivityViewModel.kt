package com.salesground.zipbolt.viewmodel

import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pInfo
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.model.ui.OngoingDataTransferUIState
import com.salesground.zipbolt.model.ui.PeerConnectionUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor() : ViewModel() {
    val currentTransferHistory: MutableList<OngoingDataTransferUIState> =
        mutableListOf(OngoingDataTransferUIState.Header)
    var collectionOfDataToTransfer: MutableList<DataToTransfer> = mutableListOf()
    val ongoingDataTransferUIStateList: MutableList<OngoingDataTransferUIState> = mutableListOf()

    private var peeredDevice: WifiP2pDevice = WifiP2pDevice().apply {
        deviceName = "Samsung Galaxy X2"
        deviceAddress = "192.021.294.24"
    }
    private var wifiP2pCurrentConnectionInfo: WifiP2pInfo = WifiP2pInfo()
    private var currentPeersList: MutableList<WifiP2pDevice> = mutableListOf()
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
                wifiP2pCurrentConnectionInfo,
                collectionOfDataToTransfer[0]
            )
    }

    fun addCurrentDataToTransferToUIState() {
        currentTransferHistory.addAll(
            collectionOfDataToTransfer.map {
                OngoingDataTransferUIState.DataItem(it)
            }
        )
        clearCollectionOfDataToTransfer()
    }

    fun expandedConnectedToPeerTransferOngoing() {
        _peerConnectionUIState.value =
            PeerConnectionUIState.ExpandedConnectedToPeerTransferOngoing(
                wifiP2pCurrentConnectionInfo,
                currentTransferHistory
            )
    }

    fun collapsedSearchingForPeers() {
        _peerConnectionUIState.value =
            PeerConnectionUIState.CollapsedSearchingForPeer(currentPeersList.size)
    }

    fun expandedSearchingForPeers() {
        _peerConnectionUIState.value =
            PeerConnectionUIState.ExpandedSearchingForPeer(currentPeersList)
    }

    fun connectedToPeer(wifiP2pInfo: WifiP2pInfo, peeredDevice: WifiP2pDevice) {
        wifiP2pCurrentConnectionInfo = wifiP2pInfo // remove this line, later after extensive tests
        this.peeredDevice = peeredDevice
        _peerConnectionUIState.value =
            PeerConnectionUIState.CollapsedConnectedToPeerNoAction(wifiP2pInfo, peeredDevice)
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

    fun addDataFromReceiveToUIState(dataToTransfer: DataToTransfer) {
        addDataToTransfer(dataToTransfer)
        addCurrentDataToTransferToUIState()
        if (_peerConnectionUIState.value is PeerConnectionUIState.ExpandedConnectedToPeerTransferOngoing) {
            expandedConnectedToPeerTransferOngoing()
        }
    }

    fun addDataToTransfer(dataToTransfer: DataToTransfer) {
        collectionOfDataToTransfer.add(dataToTransfer)
    }

    fun removeDataFromDataToTransfer(dataToTransfer: DataToTransfer) {
        collectionOfDataToTransfer.remove(dataToTransfer)
    }

    private fun clearCollectionOfDataToTransfer() {
        collectionOfDataToTransfer = mutableListOf()
    }
}