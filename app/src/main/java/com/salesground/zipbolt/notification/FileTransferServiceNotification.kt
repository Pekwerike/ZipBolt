package com.salesground.zipbolt.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat


const val FILE_TRANSFER_SERVICE_NOTIFICATION_ID = "FileTransferServiceNotificationID"
const val FILE_TRANSFER_SERVICE_CHANNEL_NAME = "ZipBolt File Transfer Service Notification"

class FileTransferServiceNotification (private val notificationManager: NotificationManager) {

    fun createFTSNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val ftsChannel = NotificationChannel(FILE_TRANSFER_SERVICE_NOTIFICATION_ID, FILE_TRANSFER_SERVICE_CHANNEL_NAME,
            NotificationCompat.PRIORITY_DEFAULT)
                .apply {
                    setDescription("This notification channel is responsible for alerting you that ZipBolt is sharing files on the background")
                    setShowBadge(false)
                }
            notificationManager.createNotificationChannel(ftsChannel)
        }
    }
}