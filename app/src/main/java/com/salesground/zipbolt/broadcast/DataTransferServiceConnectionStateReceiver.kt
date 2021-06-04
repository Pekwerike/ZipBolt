package com.salesground.zipbolt.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class DataTransferServiceConnectionStateReceiver(
    private val connectionStateListener: ConnectionStateListener
) : BroadcastReceiver() {

    companion object {
        const val ACTION_DISCONNECTED_FROM_PEER = "DataTransferServiceActionDisconnectedFromPeer"
        const val ACTION_CANNOT_CONNECT_TO_PEER_ADDRESS =
            "DataTransferServiceActionCannotConnectToPeerAddress"
    }

    interface ConnectionStateListener {
        fun disconnectedFromPeer()
        fun cannotConnectToPeerAddress()
        fun connectionBroken()
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let {
            when (intent.action) {
                ACTION_DISCONNECTED_FROM_PEER -> {
                    connectionStateListener.disconnectedFromPeer()
                }
                ACTION_CANNOT_CONNECT_TO_PEER_ADDRESS -> {
                    connectionStateListener.cannotConnectToPeerAddress()
                }
            }
        }
    }


}