package com.salesground.zipbolt.foregroundservice

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.salesground.zipbolt.MainActivity
import com.salesground.zipbolt.OPEN_MAIN_ACTIVITY_PENDING_INTENT_REQUEST_CODE
import com.salesground.zipbolt.R
import com.salesground.zipbolt.notification.FILE_TRANSFER_SERVICE_NOTIFICATION_ID

const val NO_DATA_AVAILABLE = "NoDataAvailable"
const val DATA_AVAILABLE = "DataAvailable"
const val FILE_TRANSFER_FOREGROUND_NOTIFICATION_ID = 2
const val SOCKET_PORT = 8090

fun configureNotification(context: Context): Notification {
    val openMainActivityPendingIntent: PendingIntent =
        Intent(context, MainActivity::class.java).let {
            PendingIntent.getActivity(
                context,
                OPEN_MAIN_ACTIVITY_PENDING_INTENT_REQUEST_CODE,
                it,
                0
            )
        }

    return NotificationCompat.Builder(context, FILE_TRANSFER_SERVICE_NOTIFICATION_ID)
        .apply {
            setContentIntent(openMainActivityPendingIntent)
            setContentTitle(context.getString(R.string.fileTransferServiceNotificationTitle))
            setContentText(context.getString(R.string.fileTransferServiceNotificationContentText))
            setSmallIcon(R.drawable.zipbolt_service_notification_icon)
            setPriority(NotificationCompat.PRIORITY_DEFAULT)
        }.build()
}