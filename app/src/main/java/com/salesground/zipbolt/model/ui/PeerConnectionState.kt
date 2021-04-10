package com.salesground.zipbolt.model.ui

import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pInfo
import com.salesground.zipbolt.model.DataToTransfer

/*
"Created PeerConnectionState to represent the various states of the
 persistent bottom sheet layout during peer discovery and connection"
*/
sealed class PeerConnectionState {

    object NoConnectionAction : PeerConnectionState()

    data class CollapsedSearchingForPeer(val numberOfDevicesFound: Int) : PeerConnectionState()

    data class ExpandedSearchingForPeer(val devices: MutableList<WifiP2pDevice>) :
        PeerConnectionState()

    data class CollapsedConnectedToPeer(
        val peeredDeviceConnectionInfo: WifiP2pInfo
    ) : PeerConnectionState()

    data class ExpandedConnectedToPeer(val peeredDeviceConnectionInfo: WifiP2pInfo) :
        PeerConnectionState()
}