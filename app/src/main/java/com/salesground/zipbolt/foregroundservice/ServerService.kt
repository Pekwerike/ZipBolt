package com.salesground.zipbolt.foregroundservice

import android.app.Service
import android.content.Intent
import android.os.IBinder

class ServerService : Service() {

    override fun onBind(intent: Intent): IBinder {


    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(FILE_TRANSFER_FOREGROUND_NOTIFICATION_ID, configureNotification(this))
        return START_NOT_STICKY
    }
}