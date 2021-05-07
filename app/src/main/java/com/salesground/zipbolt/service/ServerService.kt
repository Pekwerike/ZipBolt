package com.salesground.zipbolt.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.salesground.zipbolt.communicationprotocol.ZipBoltMTP
import com.salesground.zipbolt.model.MediaModel
import com.salesground.zipbolt.notification.FileTransferServiceNotification
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.*
import javax.inject.Inject

@AndroidEntryPoint
class ServerService : Service() {
    private val serverServiceBinder: ServerServiceBinder = ServerServiceBinder()
    private var mediaItemsToTransfer: MutableList<MediaModel> = mutableListOf()
    private var isDataAvailableToTransfer: Boolean = false
    private lateinit var serverSocket: ServerSocket
    @Inject
    lateinit var zipBoltMTP: ZipBoltMTP
    @Inject
    lateinit var fileTransferServiceNotification: FileTransferServiceNotification

    override fun onDestroy() {
        serverSocket.let {
            it.close()
        }
        super.onDestroy()

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
        startForeground(FILE_TRANSFER_FOREGROUND_NOTIFICATION_ID,
            fileTransferServiceNotification.configureFileTransferNotification())

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

                /*withContext(Dispatchers.Main) {
                    Toast.makeText(this@ServerService, "Connected to Client", Toast.LENGTH_SHORT)
                        .show()
                }*/

                launch(Dispatchers.IO) {
                    listenForNewMediaCollectionToTransfer(socketDOS)
                }
                launch(Dispatchers.IO){
                    listenForIncomingMediaItemsToReceive(socketDIS)
                }
            }
        }

        return START_NOT_STICKY
    }

    private fun listenForIncomingMediaItemsToReceive(socketDIS: DataInputStream){
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

    private fun listenForNewMediaCollectionToTransfer(DOS: DataOutputStream) {
        while (true) {
            if (isDataAvailableToTransfer) {
                isDataAvailableToTransfer = false
                DOS.writeUTF(DATA_AVAILABLE)
                zipBoltMTP.transferMedia(mediaItemsToTransfer, DOS)
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