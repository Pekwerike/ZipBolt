package com.salesground.zipbolt.foregroundservice

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.salesground.zipbolt.SERVER_IP_ADDRESS_KEY
import com.salesground.zipbolt.communicationprotocol.implementation.AdvancedMediaTransferProtocol
import com.salesground.zipbolt.notification.FileTransferServiceNotification
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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



    @Inject
    lateinit var fileTransferServiceNotification: FileTransferServiceNotification

    // TODO later replace this injection with the interface
    @Inject
    lateinit var advancedMediaTransferProtocol: AdvancedMediaTransferProtocol

    override fun onBind(intent: Intent): IBinder {
       return advanceClientServiceBinder
    }

    inner class AdvanceClientServiceBinder : Binder(){
        fun getClientServiceBinder() = this@AdvanceClientService
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(
            FILE_TRANSFER_FOREGROUND_NOTIFICATION_ID,
            fileTransferServiceNotification.configureFileTransferNotification()
        )
        intent?.let { mainIntent : Intent ->
            CoroutineScope(Dispatchers.IO).launch {
                serverIpAddress = mainIntent.getStringExtra(SERVER_IP_ADDRESS_KEY)!!
                val server = Socket()
                server.bind(null)
                server.connect(InetSocketAddress(serverIpAddress, SOCKET_PORT), 10000)
                val socketDIS = DataInputStream(BufferedInputStream(server.getInputStream()))
                val socketDOS = DataOutputStream(BufferedOutputStream(server.getOutputStream()))

                launch {

                }
                launch {

                }
            }
        }
        return START_NOT_STICKY
    }
    fun listenForAvailableFilesToTransfer(dataOutputStream: DataOutputStream){
        while(true){
            when(dataTransferUserEvent){
                DataTransferUserEvent.NO_DATA -> {
                    dataOutputStream.writeUTF(dataTransferUserEvent.state)
                }
                DataTransferUserEvent.DATA_AVAILABLE -> {
                    dataOutputStream.writeUTF(dataTransferUserEvent.state)

                }
                DataTransferUserEvent.CANCEL_ON_GOING_TRANSFER -> TODO()
            }

        }
    }
}