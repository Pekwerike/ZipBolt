package com.salesground.zipbolt.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri


class IncomingDataBroadcastReceiver : BroadcastReceiver() {

    companion object {
        const val INCOMING_DATA_BYTES_RECEIVED_ACTION =
            "com.salesground.speedforce.INCOMING_DATA_BYTES_RECEIVED_ACTION"
        const val INCOMING_FILE_NAME = "IncomingFileName"
        const val PERCENTAGE_OF_DATA_RECEIVED = "PercentageOfDataReceived"
        const val INCOMING_FILE_URI = "IncomingFileURI"
        const val INCOMING_FILE_SIZE = "IncomingFileSize"
        const val INCOMING_FILE_MIME_TYPE = "IncomingFileMimeType"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let {
            when (intent.action) {
                INCOMING_DATA_BYTES_RECEIVED_ACTION -> {
                    val fileName = intent.getStringExtra(INCOMING_FILE_NAME)
                    val bytesReceived = intent.getFloatExtra(PERCENTAGE_OF_DATA_RECEIVED, 0f)
                    val fileUri = intent.getParcelableExtra<Uri>(INCOMING_FILE_URI)
                }
            }
        }
    }
}