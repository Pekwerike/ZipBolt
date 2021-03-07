package com.salesground.zipbolt.foregroundservice

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.salesground.zipbolt.communicationprotocol.ZipBoltMTP
import com.salesground.zipbolt.model.MediaModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.*
import java.nio.channels.AlreadyBoundException

class ServerService : Service() {
    private val serverServiceBinder: ServerServiceBinder = ServerServiceBinder()
    private lateinit var zipBoltMTP: ZipBoltMTP
    private var mediaItemsToTransfer: MutableList<MediaModel> = mutableListOf()
    private var isDataAvailableToTransfer: Boolean = false
    private lateinit var serverSocket: ServerSocket

    override fun onDestroy() {
        serverSocket.let {
            it.close()
            Toast.makeText(this, "Server socket closed", Toast.LENGTH_SHORT).show()
        }
        super.onDestroy()
    }

    override fun onCreate() {
        super.onCreate()
        zipBoltMTP = ZipBoltMTP(this)
    }

    inner class ServerServiceBinder : Binder() {
        fun getServerServiceInstance(): ServerService {
            return this@ServerService
        }
    }

    override fun onBind(intent: Intent): IBinder {
        return serverServiceBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(FILE_TRANSFER_FOREGROUND_NOTIFICATION_ID, configureNotification(this))

        intent?.let {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    serverSocket = ServerSocket(SOCKET_PORT)
                } catch (addressAlreadyInUse: Exception) {

                }
                var client: Socket? = null
                while (true) {
                    try {
                        client = serverSocket.accept()
                        break
                    } catch (socketConnectionTimeOut: SocketTimeoutException) {
                        continue
                    }
                }
                val socketDOS = DataOutputStream(BufferedOutputStream(client!!.getOutputStream()))
                val socketDIS = DataInputStream(BufferedInputStream(client.getInputStream()))

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ServerService, "Connected to Client", Toast.LENGTH_SHORT)
                        .show()
                }

                CoroutineScope(Dispatchers.IO).launch {
                    listenForNewMediaCollectionToTransfer(socketDOS)
                }
                CoroutineScope(Dispatchers.IO).launch {
                    listenForIncomingMediaItemsToReceive(socketDIS)
                }
            }
        }

        return START_NOT_STICKY
    }

   /* private fun listenForIncomingMediaItemsToReceive(DIS: DataInputStream) {
        while (true) {
            val isMediaAvailable = DIS.readUTF()
            if (isMediaAvailable == DATA_AVAILABLE) {
                zipBoltMTP.receiveMedia(DIS)
            }
        }
    }*/
    private fun listenForIncomingMediaItemsToReceive(socketDIS: DataInputStream){
        while (true) {
            try {
                val isDataToReceiveAvailable = socketDIS.readUTF()
                if (isDataToReceiveAvailable == DATA_AVAILABLE) {
                   // Log.i("NewTransfer", "Data is now available")
                    zipBoltMTP.receiveMedia(socketDIS)
                }
            } catch (exception: Exception) {
                continue
            }
        }
    }

    private fun listenForNewMediaCollectionToTransfer(DOS: DataOutputStream) {
        while (true) {
            if (isDataAvailableToTransfer) {
                isDataAvailableToTransfer = false
                DOS.writeUTF(DATA_AVAILABLE)
                zipBoltMTP.transferMedia(mediaItemsToTransfer, DOS)
                //Log.i("NewTransfer", "Transfer completed " +
                  //      "isDataAvailableToTransfer value is ${isDataAvailableToTransfer}")
            } else {
                DOS.writeUTF(NO_DATA_AVAILABLE)
            }
        }
    }

    fun transferMediaItems(mediaItems: MutableList<MediaModel>) {
        mediaItemsToTransfer = mediaItems
        isDataAvailableToTransfer = true
        //Log.i("NewTransfer", "Items available, items count is ${mediaItemsToTransfer.size}")
    }
}