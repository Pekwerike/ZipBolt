package com.salesground.zipbolt.broadcast

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pGroup
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.os.Build

class WifiDirectBroadcastReceiver(
    private val wifiDirectBroadcastReceiverCallback: WifiDirectBroadcastReceiverCallback,
    private val connectivityManager: ConnectivityManager,
    private val wifiP2pManager: WifiP2pManager,
    private val wifiP2pChannel: WifiP2pManager.Channel
) : BroadcastReceiver() {


    interface WifiDirectBroadcastReceiverCallback {
        fun wifiOn()
        fun wifiOff()
        fun peersListAvailable(peersList: MutableList<WifiP2pDevice>)
        fun connectedToPeer(
            wifiP2pInfo: WifiP2pInfo,
            peeredDevice: WifiP2pDevice
        )

        fun wifiP2pDiscoveryStopped()
        fun wifiP2pDiscoveryStarted()
        fun disconnectedFromPeer()
    }

    @SuppressLint("MissingPermission")
    override fun onReceive(p0: Context?, p1: Intent?) {
        p1?.let { intent: Intent ->
            val action: String? = intent.action
            action.let {
                when (it) {
                    // Broadcast when Wi-Fi P2P is enabled or disabled on the device.
                    WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                        val isWifiOn = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
                        if (isWifiOn == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                            // wifiP2p ie enabled
                            wifiDirectBroadcastReceiverCallback.wifiOn()
                        } else {
                            // wifiP2p is not enabled
                            wifiDirectBroadcastReceiverCallback.wifiOff()
                        }

                    }

                    /* Broadcast when you call discoverPeers().
                        You will usually call requestPeers() to get an updated list of peers if you handle this intent in your application.
                        */
                    WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                        wifiP2pManager.requestPeers(wifiP2pChannel) { p0 ->
                            // potential peers are available
                            // Store this list in a viewModel to display it on the UI
                            p0?.deviceList?.let { collectionOfWifiP2pDevice ->
                                wifiDirectBroadcastReceiverCallback.peersListAvailable(
                                    collectionOfWifiP2pDevice.toMutableList()
                                )
                            }
                        }
                    }

                    //Broadcast when the state of the device's Wi-Fi connection changes.
                    WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                        wifiP2pManager.requestConnectionInfo(
                            wifiP2pChannel
                        ) { p0 ->
                            p0?.let { wifiP2pInfo ->
                                // send connected device the connection info to the mainActivityViewModel
                                // so we can create a socket connection and begin data transfer
                                if (wifiP2pInfo.groupFormed) {
                                    val wifiP2pGroup =
                                        intent.getParcelableExtra<WifiP2pGroup>(WifiP2pManager.EXTRA_WIFI_P2P_GROUP)!!
                                    val connectedDevice: WifiP2pDevice =
                                        if (wifiP2pInfo.isGroupOwner) {
                                            wifiP2pGroup.clientList.first()
                                        } else {
                                            wifiP2pGroup.owner
                                        }

                                    wifiDirectBroadcastReceiverCallback.connectedToPeer(
                                        wifiP2pInfo,
                                        connectedDevice
                                    )
                                }else{
                                    // send no action
                                    wifiDirectBroadcastReceiverCallback.disconnectedFromPeer()
                                }
                            }
                        }
                    }

                    // Broadcast when a device's details have changed, such as the device's name.
                    WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {

                    }

                    WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION -> {
                        val discoveryState = intent.getIntExtra(
                            WifiP2pManager.EXTRA_DISCOVERY_STATE,
                            -1
                        )
                        when (discoveryState) {
                            WifiP2pManager.WIFI_P2P_DISCOVERY_STARTED -> {
                                wifiDirectBroadcastReceiverCallback.wifiP2pDiscoveryStarted()
                            }
                            WifiP2pManager.WIFI_P2P_DISCOVERY_STOPPED -> {
                                wifiDirectBroadcastReceiverCallback.wifiP2pDiscoveryStopped()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun isDeviceConnected(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val networkCapabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_WIFI_P2P) -> true
                else -> false
            }
        } else {
            return connectivityManager.activeNetworkInfo?.isConnected ?: false
        }
    }
}