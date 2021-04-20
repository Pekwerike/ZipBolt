package com.salesground.zipbolt.foregroundservice

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.salesground.zipbolt.IS_SERVER_KEY
import com.salesground.zipbolt.SERVER_IP_ADDRESS_KEY
import com.salesground.zipbolt.communicationprotocol.MediaTransferProtocol
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.notification.FileTransferServiceNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject

class AdvanceServerService : Service() {
    private val advanceServerService: AdvanceServerServiceBinder = AdvanceServerServiceBinder()
    private var dataTransferUserEvent = DataTransferUserEvent.NO_DATA

    @Inject
    lateinit var mediaTransferProtocol: MediaTransferProtocol

    @Inject
    lateinit var fileTransferServiceNotification: FileTransferServiceNotification

    inner class AdvanceServerServiceBinder : Binder() {
        fun getServerServiceInstance(): AdvanceServerService {
            return this@AdvanceServerService
        }
    }


    private var dataCollection = AtomicReference<MutableList<DataToTransfer>>()
    private val mutex = Mutex()

    suspend fun transferData(dataCollectionSelected: MutableList<DataToTransfer>) {
        mutex.withLock {
            while (dataTransferUserEvent == DataTransferUserEvent.DATA_AVAILABLE) {
                // get stuck here

            }
            // when datatransferUserEvent shows data is not available then assign the new data
            dataCollection.set(dataCollectionSelected)
            dataTransferUserEvent = DataTransferUserEvent.DATA_AVAILABLE
        }
    }


    override fun onBind(intent: Intent): IBinder {
        return advanceServerService
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(
            FILE_TRANSFER_FOREGROUND_NOTIFICATION_ID,
            fileTransferServiceNotification.configureFileTransferNotification()
        )


        intent?.let {
            val isServer = intent.getBooleanExtra(IS_SERVER_KEY, false)
            when (isServer) {
                false -> configureClientSocket(intent.getStringExtra(SERVER_IP_ADDRESS_KEY)!!)
                true -> configureServerSocket()
            }
        }
        return START_NOT_STICKY
    }

    private fun configureServerSocket() {
        CoroutineScope(Dispatchers.IO).launch {

            val serverSocket = ServerSocket(SOCKET_PORT)
            val client: Socket = serverSocket.accept()
            val socketDOS = DataOutputStream(BufferedOutputStream(client.getOutputStream()))
            val socketDIS = DataInputStream(BufferedInputStream(client.getInputStream()))

            launch {
                listenForMediaToTransfer(socketDOS)
            }
            delay(300)
            listenForMediaToReceive(socketDIS)
        }
    }

    private fun configureClientSocket(serverIpAddress: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val server = Socket()
            server.bind(null)
            server.connect(InetSocketAddress(serverIpAddress, SOCKET_PORT), 1000)
            val socketDOS = DataOutputStream(BufferedOutputStream(server.getOutputStream()))
            val socketDIS = DataInputStream(BufferedInputStream(server.getInputStream()))
            launch {
                listenForMediaToTransfer(socketDOS)
            }
            delay(300)
            listenForMediaToReceive(socketDIS)
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun listenForMediaToTransfer(dataOutputStream: DataOutputStream) {
        while (true) {
            when (dataTransferUserEvent) {
                DataTransferUserEvent.NO_DATA -> dataOutputStream.writeUTF(dataTransferUserEvent.state)
                DataTransferUserEvent.DATA_AVAILABLE -> {
                    dataCollection.get().forEach {
                        dataOutputStream.writeUTF(dataTransferUserEvent.state)
                        mediaTransferProtocol.transferMedia(it, dataOutputStream)
                    }
                    dataTransferUserEvent = DataTransferUserEvent.NO_DATA
                }

                DataTransferUserEvent.CANCEL_ON_GOING_TRANSFER -> {
                    mediaTransferProtocol.cancelCurrentTransfer(
                        transferMetaData =
                        MediaTransferProtocol.TransferMetaData.CANCEL_ACTIVE_RECEIVE
                    )
                }
            }
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun listenForMediaToReceive(dataInputStream: DataInputStream) {
        while (true) {
            when (dataInputStream.readUTF()) {
                DataTransferUserEvent.NO_DATA.state -> continue
                DataTransferUserEvent.DATA_AVAILABLE.state -> {
                    mediaTransferProtocol.receiveMedia(dataInputStream)
                }
                DataTransferUserEvent.CANCEL_ON_GOING_TRANSFER.state -> {
                    mediaTransferProtocol.cancelCurrentTransfer(
                        transferMetaData = MediaTransferProtocol.TransferMetaData.CANCEL_ACTIVE_RECEIVE
                    )
                }
            }
        }
    }
}