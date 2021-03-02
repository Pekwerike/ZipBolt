package com.salesground.zipbolt.foregroundservice

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat

class ClientService : Service() {
    private val clientServiceBinder = ClientServiceBinder()

    inner class ClientServiceBinder : Binder() {
        fun getClientServiceBinder() = this@ClientService
    }
    override fun onBind(intent: Intent): IBinder {
        return clientServiceBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val serviceNotification = NotificationCompat.Builder(this, )

        return START_NOT_STICKY
    }
}