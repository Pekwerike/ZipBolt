package com.salesground.zipbolt.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.salesground.zipbolt.MainActivity
import com.salesground.zipbolt.OPEN_MAIN_ACTIVITY_PENDING_INTENT_REQUEST_CODE
import com.salesground.zipbolt.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton


const val FILE_TRANSFER_SERVICE_NOTIFICATION_ID = "FileTransferServiceNotificationID"
const val FILE_TRANSFER_SERVICE_CHANNEL_NAME = "ZipBolt File Transfer Service Notification"


class FileTransferServiceNotification @Inject
constructor(
    private val notificationManager: NotificationManager,
    @ApplicationContext private val context: Context
) : LifecycleObserver {


    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun createFTSNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val ftsChannel = NotificationChannel(
                FILE_TRANSFER_SERVICE_NOTIFICATION_ID, FILE_TRANSFER_SERVICE_CHANNEL_NAME,
                NotificationCompat.PRIORITY_DEFAULT
            )
                .apply {
                    setDescription("This notification channel is responsible for alerting you that ZipBolt is sharing files on the background")
                    setShowBadge(false)
                }
            notificationManager.createNotificationChannel(ftsChannel)
        }
    }

    fun configureFileTransferNotification(): Notification {
        val openMainActivityPendingIntent: PendingIntent =
            Intent(context, MainActivity::class.java).let {
                it.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                PendingIntent.getActivity(
                    context,
                    OPEN_MAIN_ACTIVITY_PENDING_INTENT_REQUEST_CODE,
                    it,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            }

        return NotificationCompat.Builder(context, FILE_TRANSFER_SERVICE_NOTIFICATION_ID)
            .apply {
                setContentTitle(context.getString(R.string.fileTransferServiceNotificationTitle))
                setContentText(context.getString(R.string.fileTransferServiceNotificationContentText))
                setContentIntent(openMainActivityPendingIntent)
                setSmallIcon(R.drawable.zipbolt_service_notification_icon)
                priority = NotificationCompat.PRIORITY_MAX
            }.build()
    }
}