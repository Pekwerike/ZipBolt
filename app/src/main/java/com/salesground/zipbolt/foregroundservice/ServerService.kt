package com.salesground.zipbolt.foregroundservice

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.salesground.zipbolt.communicationprotocol.ZipBoltMTP
import com.salesground.zipbolt.model.MediaModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.ServerSocket
import java.net.Socket

class ServerService : Service() {
    private val serverServiceBinder: ServerServiceBinder = ServerServiceBinder()
    private lateinit var zipBoltMTP: ZipBoltMTP
    private var mediaItemsToTransfer : MutableList<MediaModel> = mutableListOf()

    override fun onCreate() {
        super.onCreate()
        zipBoltMTP = ZipBoltMTP(this)
    }

    inner class ServerServiceBinder : Binder() {
        fun getServerServiceInstance(): ServerService {
            return this@ServerService
        }
    }

    override fun onBind(intent: Intent): IBinder {
        return serverServiceBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(FILE_TRANSFER_FOREGROUND_NOTIFICATION_ID, configureNotification(this))

        intent?.let {
            CoroutineScope(Dispatchers.IO).launch {
                val server = ServerSocket(SOCKET_PORT)
                server.accept()
            }
        }

        return START_NOT_STICKY
    }

    fun transferMediaItems(mediaItems : MutableList<MediaModel>){
        mediaItemsToTransfer = mediaItems
    }
}