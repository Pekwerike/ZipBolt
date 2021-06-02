package com.salesground.zipbolt.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class SendDataBroadcastReceiver(
    private val sendDataButtonClickedListener: SendDataButtonClickedListener
): BroadcastReceiver() {

    companion object{
        const val ACTION_SEND_DATA_BUTTON_CLICKED = "ActionSendDataButtonClicked"
    }

    interface SendDataButtonClickedListener{
        fun sendDataButtonClicked()
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let{
            when(intent.action) {
                ACTION_SEND_DATA_BUTTON_CLICKED -> {
                    sendDataButtonClickedListener.sendDataButtonClicked()
                }
            }
        }
    }
}