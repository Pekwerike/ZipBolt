package com.salesground.zipbolt.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.p2p.WifiP2pManager

class UpgradedWifiDirectBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let{
            when(intent.action){
                WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                    // scenario 1 -> new peers have been discovered
                }
            }
        }
    }
}