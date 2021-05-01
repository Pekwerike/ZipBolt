package com.salesground.zipbolt.model.ui

import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pInfo

/*
"Created PeerConnectionUIState to represent the various states of the
 persistent bottom sheet layout during peer discovery and connection"
*/
sealed class PeerConnectionUIState {

    object NoConnectionUIAction : PeerConnectionUIState()

    data class CollapsedSearchingForPeer(val numberOfDevicesFound: Int) : PeerConnectionUIState()

    data class ExpandedSearchingForPeer(val devices: MutableList<WifiP2pDevice>) :
        PeerConnectionUIState()

    data class CollapsedConnectedToPeer(
        val peeredDeviceConnectionInfo: WifiP2pInfo
    ) : PeerConnectionUIState()

    data class ExpandedConnectedToPeer(val peeredDeviceConnectionInfo: WifiP2pInfo) :
        PeerConnectionUIState()
}