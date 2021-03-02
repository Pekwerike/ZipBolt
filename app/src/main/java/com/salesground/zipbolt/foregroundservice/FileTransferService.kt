package com.salesground.zipbolt.foregroundservice

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat

class FileTransferService : Service() {
    private val fileTransferServiceBinder = FileTransferServiceBinder()

    inner class FileTransferServiceBinder : Binder() {
        fun getFileTransferService() = this@FileTransferService
    }
    override fun onBind(intent: Intent): IBinder {
        return fileTransferServiceBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val serviceNotification = NotificationCompat.Builder(this, )

        return START_NOT_STICKY
    }
}