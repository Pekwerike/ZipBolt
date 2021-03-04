package com.salesground.zipbolt.foregroundservice

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder

class ServerService : Service() {
    private val serverServiceBinder : ServerServiceBinder = ServerServiceBinder()

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
        return START_NOT_STICKY
    }
}