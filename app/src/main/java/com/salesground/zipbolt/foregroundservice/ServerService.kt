package com.salesground.zipbolt.foregroundservice

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
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
import java.net.BindException
import java.net.ServerSocket
import java.net.SocketException
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
                while(true) {
                    try {
                        serverSocket = ServerSocket(SOCKET_PORT)
                        break
                    } catch (addressAlreadyInUse: SocketException) {
                        try {
                            serverSocket.bind(null)
                        }catch (alreadyBound: Exception){
                            break
                        }
                        continue
                    }
                }
                val client = serverSocket.accept()
                val socketDOS = DataOutputStream(BufferedOutputStream(client.getOutputStream()))
                val socketDIS = DataInputStream(BufferedInputStream(client.getInputStream()))

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ServerService, "Connected to Client", Toast.LENGTH_SHORT)
                        .show()
                }

                CoroutineScope(Dispatchers.IO).launch {
                    listenForNewMediaCollectionToTransfer(socketDOS)
                }
                CoroutineScope(Dispatchers.IO).launch {

                }
            }
        }

        return START_NOT_STICKY
    }

    private suspend fun listenForIncomingMediaItemsToReceive(DIS: DataInputStream) {
        while (true) {
            val isMediaAvailable = DIS.readUTF()
            if (isMediaAvailable == DATA_AVAILABLE) {
                zipBoltMTP.receiveMedia(DIS)
            }
        }
    }

    private suspend fun listenForNewMediaCollectionToTransfer(DOS: DataOutputStream) {

        while (true) {
            if (isDataAvailableToTransfer) {
                DOS.writeUTF(DATA_AVAILABLE)
                zipBoltMTP.transferMedia(mediaItemsToTransfer, DOS)
                isDataAvailableToTransfer = false
            } else {
                DOS.writeUTF(NO_DATA_AVAILABLE)
            }
        }
    }

    fun transferMediaItems(mediaItems: MutableList<MediaModel>) {
        mediaItemsToTransfer = mediaItems
        isDataAvailableToTransfer = true
    }
}