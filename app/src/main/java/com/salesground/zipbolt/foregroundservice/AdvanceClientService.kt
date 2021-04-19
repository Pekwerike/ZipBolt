package com.salesground.zipbolt.foregroundservice

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.salesground.zipbolt.SERVER_IP_ADDRESS_KEY
import com.salesground.zipbolt.communicationprotocol.MediaTransferProtocol
import com.salesground.zipbolt.communicationprotocol.implementation.AdvancedMediaTransferProtocol
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.notification.FileTransferServiceNotification
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.InetSocketAddress
import java.net.Socket
import javax.inject.Inject


@AndroidEntryPoint
class AdvanceClientService : Service() {
    private val advanceClientServiceBinder = AdvanceClientServiceBinder()
    private var dataTransferUserEvent = DataTransferUserEvent.NO_DATA
    private var serverIpAddress: String = ""

    // TODO, research to see how this can be replaced with flow
    private var dataToTransfer: MutableList<DataToTransfer> = mutableListOf()


    @Inject
    lateinit var fileTransferServiceNotification: FileTransferServiceNotification

    // TODO later replace this injection with the interface
    @Inject
    lateinit var advancedMediaTransferProtocol: AdvancedMediaTransferProtocol

    override fun onBind(intent: Intent): IBinder {
        return advanceClientServiceBinder
    }

    inner class AdvanceClientServiceBinder : Binder() {
        fun getClientServiceBinder() = this@AdvanceClientService
    }

    fun dataToTransferAvailable(dataToTransfer: DataToTransfer){
        this.dataToTransfer.add(dataToTransfer)
    }

    fun cancelActiveTransfer(){
        advancedMediaTransferProtocol.cancelCurrentTransfer(transferMetaData =
        MediaTransferProtocol.TransferMetaData.CANCEL_ACTIVE_RECEIVE)
    }

    fun cancelActiveReceive(){
        when(dataTransferUserEvent){
            DataTransferUserEvent.NO_DATA -> {
                // not transferring any data, but wants to stop receiving data from peer,
                // so send a message to peer to cancel ongoing transfer
                dataTransferUserEvent = DataTransferUserEvent.CANCEL_ON_GOING_TRANSFER
            }
            DataTransferUserEvent.DATA_AVAILABLE -> {
                // transferring data to peer but wants to stop receiving from peer,
                // so send a message to the peer to stop reading for new bytes while I stop sending
                advancedMediaTransferProtocol.cancelCurrentTransfer(transferMetaData =
                MediaTransferProtocol.TransferMetaData.KEEP_RECEIVING_BUT_CANCEL_ACTIVE_TRANSFER)
            }
            DataTransferUserEvent.CANCEL_ON_GOING_TRANSFER -> TODO()
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(
            FILE_TRANSFER_FOREGROUND_NOTIFICATION_ID,
            fileTransferServiceNotification.configureFileTransferNotification()
        )
        intent?.let { mainIntent: Intent ->
            CoroutineScope(Dispatchers.IO).launch {
                serverIpAddress = mainIntent.getStringExtra(SERVER_IP_ADDRESS_KEY)!!
                val server = Socket()
                server.bind(null)
                server.connect(InetSocketAddress(serverIpAddress, SOCKET_PORT), 10000)
                val socketDIS = DataInputStream(BufferedInputStream(server.getInputStream()))
                val socketDOS = DataOutputStream(BufferedOutputStream(server.getOutputStream()))

                launch {
                    listenForAvailableMediaToTransfer(socketDOS)
                }
                delay(500) // little delay to make sure the peer has already written to the socket
                launch {
                    listenForAvailableMediaToReceive(socketDIS)
                }
            }
        }
        return START_NOT_STICKY
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun listenForAvailableMediaToReceive(dataInputStream: DataInputStream) {
        while (true) {
            when (dataInputStream.readUTF()) {
                DataTransferUserEvent.NO_DATA.state -> continue
                DataTransferUserEvent.DATA_AVAILABLE.state -> {
                    advancedMediaTransferProtocol.receiveMedia(dataInputStream)
                }
                DataTransferUserEvent.CANCEL_ON_GOING_TRANSFER.state -> {
                    advancedMediaTransferProtocol.cancelCurrentTransfer(
                        transferMetaData = MediaTransferProtocol.TransferMetaData.CANCEL_ACTIVE_RECEIVE
                    )
                }
            }
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun listenForAvailableMediaToTransfer(dataOutputStream: DataOutputStream) {
        while (true) {
            when (dataTransferUserEvent) {
                DataTransferUserEvent.NO_DATA -> {
                    dataOutputStream.writeUTF(dataTransferUserEvent.state)
                }
                DataTransferUserEvent.DATA_AVAILABLE -> {
                    dataToTransfer.forEach {
                        dataOutputStream.writeUTF(dataTransferUserEvent.state)
                        advancedMediaTransferProtocol.transferMedia(it, dataOutputStream)
                        dataToTransfer.remove(it)
                    }
                    dataTransferUserEvent = DataTransferUserEvent.NO_DATA
                }
                DataTransferUserEvent.CANCEL_ON_GOING_TRANSFER -> {
                    dataOutputStream.writeUTF(dataTransferUserEvent.state)
                    dataTransferUserEvent = DataTransferUserEvent.NO_DATA
                }
            }

        }
    }
}