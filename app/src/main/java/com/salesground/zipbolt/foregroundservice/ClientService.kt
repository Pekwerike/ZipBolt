package com.salesground.zipbolt.foregroundservice

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.salesground.zipbolt.MainActivity
import com.salesground.zipbolt.OPEN_MAIN_ACTIVITY_PENDING_INTENT_REQUEST_CODE
import com.salesground.zipbolt.R
import com.salesground.zipbolt.SERVER_IP_ADDRESS_KEY
import com.salesground.zipbolt.communicationprotocol.ZipBoltMTP
import com.salesground.zipbolt.model.MediaModel
import com.salesground.zipbolt.notification.FILE_TRANSFER_SERVICE_NOTIFICATION_ID
import com.salesground.zipbolt.notification.FileTransferServiceNotification
import dagger.hilt.android.AndroidEntryPoint
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
import java.net.SocketException
import javax.inject.Inject

@AndroidEntryPoint
class ClientService : Service() {
    private val clientServiceBinder = ClientServiceBinder()
    private var serverIpAddress: String = ""
    private var isDataToTransferAvailable: Boolean = false
    @Inject
    lateinit var zipBoltMTP: ZipBoltMTP
    @Inject
    lateinit var fileTransferServiceNotification: FileTransferServiceNotification

    private var mediaItemsToTransfer: MutableList<MediaModel> = mutableListOf()


    inner class ClientServiceBinder : Binder() {
        fun getClientServiceBinder() = this@ClientService
    }

    override fun onBind(intent: Intent): IBinder {
        return clientServiceBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(FILE_TRANSFER_FOREGROUND_NOTIFICATION_ID,
        fileTransferServiceNotification.configureFileTransferNotification())

        intent?.let { mainIntent: Intent ->
            CoroutineScope(Dispatchers.IO).launch {
                serverIpAddress = mainIntent.getStringExtra(SERVER_IP_ADDRESS_KEY)!!
                val server = Socket()
                server.bind(null)
                server.connect(InetSocketAddress(serverIpAddress, SOCKET_PORT), 100000)
                val socketDIS = DataInputStream(BufferedInputStream(server.getInputStream()))
                val socketDOS = DataOutputStream(BufferedOutputStream(server.getOutputStream()))
                /*withContext(Dispatchers.Main) {
                    Toast.makeText(this@ClientService, "Connected to server", Toast.LENGTH_SHORT)
                        .show()
                }*/
                launch (Dispatchers.IO){
                    listenForAvailableFilesToTransfer(socketDOS)
                }

                launch(Dispatchers.IO){
                    listenForIncomingFiles(socketDIS)
                }
            }
        }
        return START_NOT_STICKY
    }


    private fun listenForAvailableFilesToTransfer(DOS: DataOutputStream) {
        while (true) {
            if (isDataToTransferAvailable) {
                isDataToTransferAvailable = false
                DOS.writeUTF(DATA_AVAILABLE)
                zipBoltMTP.transferMedia(mediaItemsToTransfer, DOS)

            } else {
                DOS.writeUTF(NO_DATA_AVAILABLE)
            }
        }
    }

    private fun listenForIncomingFiles(socketDIS: DataInputStream) {
        while (true) {
            try {
                val isDataToReceiveAvailable = socketDIS.readUTF()
                if (isDataToReceiveAvailable == DATA_AVAILABLE) {
                    zipBoltMTP.receiveMedia(socketDIS)
                }
            } catch (exception: Exception) {
                continue
            }
        }
    }

    fun transferMediaItems(mediaCollection: MutableList<MediaModel>) {
        mediaItemsToTransfer = mediaCollection
        isDataToTransferAvailable = true
    }

}