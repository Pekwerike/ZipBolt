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

    }
}