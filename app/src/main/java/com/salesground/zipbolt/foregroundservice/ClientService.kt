package com.salesground.zipbolt.foregroundservice

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.salesground.zipbolt.MainActivity
import com.salesground.zipbolt.OPEN_MAIN_ACTIVITY_PENDING_INTENT_REQUEST_CODE
import com.salesground.zipbolt.R
import com.salesground.zipbolt.notification.FILE_TRANSFER_SERVICE_NOTIFICATION_ID


const val CLIENT_SERVICE_FOREGROUND_NOTIFICATION_ID = 2

class ClientService : Service() {
    private val clientServiceBinder = ClientServiceBinder()

    inner class ClientServiceBinder : Binder() {
        fun getClientServiceBinder() = this@ClientService
    }

    override fun onBind(intent: Intent): IBinder {
        return clientServiceBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(CLIENT_SERVICE_FOREGROUND_NOTIFICATION_ID, configureNotification())



        return START_NOT_STICKY
    }

    private fun configureNotification(): Notification {
        val openMainActivityPendingIntent: PendingIntent =
            Intent(this, MainActivity::class.java).let {
                PendingIntent.getActivity(
                    this,
                    OPEN_MAIN_ACTIVITY_PENDING_INTENT_REQUEST_CODE,
                    it,
                    0
                )
            }
        return NotificationCompat.Builder(this, FILE_TRANSFER_SERVICE_NOTIFICATION_ID)
            .apply {
                setContentIntent(openMainActivityPendingIntent)
                setContentTitle(getString(R.string.fileTransferServiceNotificationTitle))
                setContentText(getString(R.string.fileTransferServiceNotificationContentText))
                setSmallIcon(R.drawable.zipbolt_service_notification_icon)
                setPriority(NotificationCompat.PRIORITY_DEFAULT)
            }.build()
    }
}