package com.salesground.zipbolt.model.ui

import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pInfo
import com.salesground.zipbolt.model.DataToTransfer

/*
"Created PeerConnectionUIState to represent the various states of the
 persistent bottom sheet layout during peer discovery and connection"
*/
sealed class PeerConnectionUIState {

    object NoConnectionUIAction : PeerConnectionUIState()

    data class CollapsedConnectedToPeerTransferOngoing(
        val peeredDeviceConnectionInfo: WifiP2pInfo,

        ) : PeerConnectionUIState()

    data class ExpandedConnectedToPeerTransferOngoing(
        val peeredDeviceConnectionInfo: WifiP2pInfo
    ) : PeerConnectionUIState()
}