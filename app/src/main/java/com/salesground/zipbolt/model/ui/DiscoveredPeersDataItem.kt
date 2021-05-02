package com.salesground.zipbolt.model.ui

import android.net.wifi.p2p.WifiP2pDevice

sealed class DiscoveredPeersDataItem(val id: String) {
    object Header : DiscoveredPeersDataItem(id = "discoveredPeersDataItemHeader")
    class DiscoveredPeer(val wifiP2pDevice: WifiP2pDevice) :
        DiscoveredPeersDataItem(id = wifiP2pDevice.deviceName)
}