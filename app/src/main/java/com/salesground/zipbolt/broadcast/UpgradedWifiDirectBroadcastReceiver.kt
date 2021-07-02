package com.salesground.zipbolt.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.net.wifi.p2p.WifiP2pManager
import android.os.Build

class UpgradedWifiDirectBroadcastReceiver(context: Context) : BroadcastReceiver() {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let {
            when (intent.action) {
                WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                    // check if we are already connected to a wifi p2p device
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {

                        connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                            ?.let { networkCapabilities: NetworkCapabilities ->
                                if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) &&
                                    networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_WIFI_P2P)
                                ) {
                                    // request for connection info

                                }
                            }
                    } else {
                        // use the NetworkInfo class to check if we are connected or not
                        intent.getParcelableExtra<NetworkInfo>(WifiP2pManager.EXTRA_NETWORK_INFO)
                            ?.let { networkInfo: NetworkInfo ->
                                if (networkInfo.isConnected) {
                                    // request for connection info
                                }
                            }
                    }
                }
                else -> {

                }
            }
        }
    }
}