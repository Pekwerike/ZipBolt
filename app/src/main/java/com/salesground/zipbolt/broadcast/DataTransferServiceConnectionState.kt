package com.salesground.zipbolt.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class DataTransferServiceConnectionState : BroadcastReceiver() {

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

                }
                ACTION_CANNOT_CONNECT_TO_PEER_ADDRESS -> {

                }
            }
        }
    }


}