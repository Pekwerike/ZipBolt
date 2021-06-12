package com.salesground.zipbolt.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.salesground.zipbolt.notification.FileTransferServiceNotification
import dagger.hilt.android.AndroidEntryPoint
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket
import javax.inject.Inject

@AndroidEntryPoint
class OneDirectionalDataTransferService : Service() {

    // service binder variables
    private val oneDirectionalDataTransferServiceBinder = OneDirectionalDataTransferServiceBinder()

    inner class OneDirectionalDataTransferServiceBinder : Binder() {
        fun getInstance(): OneDirectionalDataTransferService {
            return this@OneDirectionalDataTransferService
        }
    }

    // tcp sockets variables
    private lateinit var socket: Socket
    private lateinit var socketDOS: DataOutputStream
    private lateinit var socketDIS : DataInputStream

    // notification variables
    @Inject
    lateinit var fileTransferServiceNotification: FileTransferServiceNotification

    override fun onBind(intent: Intent): IBinder {
        return oneDirectionalDataTransferServiceBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }
}