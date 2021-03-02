package com.salesground.zipbolt.foregroundservice

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.salesground.zipbolt.R
import com.salesground.zipbolt.notification.FILE_TRANSFER_SERVICE_NOTIFICATION_ID

class ClientService : Service() {
    private val clientServiceBinder = ClientServiceBinder()

    inner class ClientServiceBinder : Binder() {
        fun getClientServiceBinder() = this@ClientService
    }
    override fun onBind(intent: Intent): IBinder {
        return clientServiceBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val serviceNotification = NotificationCompat.Builder(this, FILE_TRANSFER_SERVICE_NOTIFICATION_ID)
            .apply {
                setContentTitle(getString(R.string.fileTransferServiceNotificationTitle))
                setContentText(getString(R.string.))
            }

        return START_NOT_STICKY
    }
}