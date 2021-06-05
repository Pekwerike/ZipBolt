package com.salesground.zipbolt.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.collection.ArraySet
import androidx.collection.arraySetOf
import com.salesground.zipbolt.model.DataToTransfer


class IncomingDataBroadcastReceiver(private val dataReceiveListener: DataReceiveListener) :
    BroadcastReceiver() {

    val completedReceiveSet: ArraySet<String> = arraySetOf()

    interface DataReceiveListener {
        fun onDataReceive(
            dataDisplayName: String,
            dataUri: Uri?,
            dataSize: Long,
            dataType: Int,
            percentTransferred: Float = 0f,
            transferStatus: Int = DataToTransfer.TransferStatus.TRANSFER_WAITING.value
        )

        fun totalFileReceiveComplete()
    }

    companion object {

        const val INCOMING_FILE_TRANSFER_STATUS = "IncomingFileTransferStatus"
        const val ACTION_TOTAL_FILE_RECEIVE_COMPLETE = "TotalFileReceiveComplete"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let {
            when (intent.action) {

                ACTION_TOTAL_FILE_RECEIVE_COMPLETE -> {
                    dataReceiveListener.totalFileReceiveComplete()
                }
                else -> {

                }
            }
        }
    }
}