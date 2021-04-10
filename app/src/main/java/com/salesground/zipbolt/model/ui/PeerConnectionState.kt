package com.salesground.zipbolt.model.ui

import android.net.wifi.p2p.WifiP2pDevice
import com.salesground.zipbolt.model.DataToTransfer

sealed class PeerConnectionState

object NoConnectionAction : PeerConnectionState()

data class CollapsedSearchingForPeer(val numberOfDevicesFound: Int) : PeerConnectionState()

data class ExpandedSearchingForPeer(val devices: MutableList<WifiP2pDevice>) : PeerConnectionState()

data class CollapsedConnectedToPeer(
    val peeredDevice: WifiP2pDevice,
    val currentFileTransfer: DataToTransfer
) : PeerConnectionState()

data class ExpandeedConnectedToPeer(val peeredDevice : WifiP2pDevice) : PeerConnectionState()