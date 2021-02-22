package com.salesground.speedforce.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.p2p.WifiP2pManager
import com.salesground.speedforce.MainActivity

class WifiDirectBroadcastReceiver(
    private val mainActivity: MainActivity,
    private val wifiP2pManager: WifiP2pManager,
    private val wifiP2pChannel: WifiP2pManager.Channel
) : BroadcastReceiver() {

    override fun onReceive(p0: Context?, p1: Intent?) {
        p1?.let { intent: Intent ->
            val action: String? = intent.action
            action?.let {
                when (it) {
                    // Broadcast when Wi-Fi P2P is enabled or disabled on the device.
                    WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                        val isWifiOn = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
                        if(isWifiOn == WifiP2pManager.WIFI_P2P_STATE_ENABLED){
                            // wifiP2p ie enabled
                            mainActivity.wifiP2pState(isEnabled = true)
                        }else {
                            // wifiP2p is not enabled
                            mainActivity.wifiP2pState(isEnabled = false)
                        }

                    }

                    /* Broadcast when you call discoverPeers().
                        You will usually call requestPeers() to get an updated list of peers if you handle this intent in your application.
                        */
                    WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                    }

                    //Broadcast when the state of the device's Wi-Fi connection changes.
                    WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {

                    }

                    // Broadcast when a device's details have changed, such as the device's name.
                    WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {

                    }
                }
            }
        }
    }
}