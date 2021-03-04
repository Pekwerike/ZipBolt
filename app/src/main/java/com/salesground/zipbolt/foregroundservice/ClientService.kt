package com.salesground.zipbolt.foregroundservice

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.salesground.zipbolt.MainActivity
import com.salesground.zipbolt.OPEN_MAIN_ACTIVITY_PENDING_INTENT_REQUEST_CODE
import com.salesground.zipbolt.R
import com.salesground.zipbolt.SERVER_IP_ADDRESS_KEY
import com.salesground.zipbolt.communicationprotocol.ZipBoltMTP
import com.salesground.zipbolt.model.MediaModel
import com.salesground.zipbolt.notification.FILE_TRANSFER_SERVICE_NOTIFICATION_ID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.InetSocketAddress
import java.net.Socket



class ClientService : Service() {
    private val clientServiceBinder = ClientServiceBinder()
    private var serverIpAddress: String = ""
    private var isDataToTransferAvailable: Boolean = false
    private lateinit var zipBoltMTP: ZipBoltMTP
    private var mediaItemsToTransfer: MutableList<MediaModel> = mutableListOf()


    inner class ClientServiceBinder : Binder() {
        fun getClientServiceBinder() = this@ClientService
    }

    override fun onBind(intent: Intent): IBinder {
        return clientServiceBinder
    }

    override fun onCreate() {
        super.onCreate()
        zipBoltMTP = ZipBoltMTP(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(FILE_TRANSFER_FOREGROUND_NOTIFICATION_ID, configureNotification(this))

        intent?.let { mainIntent: Intent ->
            CoroutineScope(Dispatchers.IO).launch {
                serverIpAddress = mainIntent.getStringExtra(SERVER_IP_ADDRESS_KEY)!!
                val server = Socket()
                server.bind(null)
                while (true) {
                    try {
                        server.connect(InetSocketAddress(serverIpAddress, 8090), 100000)
                        break
                    } catch (connectionException: Exception) {
                        continue
                    }
                }
                val socketDIS = DataInputStream(BufferedInputStream(server.getInputStream()))
                val socketDOS = DataOutputStream(BufferedOutputStream(server.getOutputStream()))

                CoroutineScope(Dispatchers.IO).launch {
                    listenForAvailableFilesToTransfer(socketDOS)
                }

                CoroutineScope(Dispatchers.IO).launch {
                    listenForIncomingFiles(socketDIS)
                }
            }
        }
        return START_NOT_STICKY
    }

    private suspend fun listenForAvailableFilesToTransfer(socketDOS: DataOutputStream) {
        while (true) {
            if (!isDataToTransferAvailable) {
                socketDOS.writeUTF(NO_DATA_AVAILABLE)
            } else {
                socketDOS.writeUTF(DATA_AVAILABLE)
                zipBoltMTP.transferMedia(mediaItemsToTransfer, socketDOS)
                isDataToTransferAvailable = false
            }
        }
    }

    private suspend fun listenForIncomingFiles(socketDIS: DataInputStream) {
        while (true) {
            val isDataToReceiveAvailable = socketDIS.readUTF()
            if (isDataToReceiveAvailable == DATA_AVAILABLE) {
                zipBoltMTP.receiveMedia(socketDIS)
            }
        }
    }

    fun transferMediaItems(mediaCollection: MutableList<MediaModel>) {
        mediaItemsToTransfer = mediaCollection
        isDataToTransferAvailable = true
    }

}