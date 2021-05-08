package com.salesground.zipbolt.service

import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.salesground.zipbolt.IS_SERVER_KEY
import com.salesground.zipbolt.SERVER_IP_ADDRESS_KEY
import com.salesground.zipbolt.broadcast.IncomingDataBroadcastReceiver
import com.salesground.zipbolt.communicationprotocol.MediaTransferProtocol
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.notification.FileTransferServiceNotification
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import javax.inject.Inject

class DataTransferService : Service() {
    companion object {
        const val IS_SERVER: String = "IsDeviceTheServer"
        const val SERVER_IP_ADDRESS = "ServerIpAddress"
    }

    private val dataTransferService: DataTransferServiceBinder = DataTransferServiceBinder()
    private var dataTransferUserEvent = DataTransferUserEvent.NO_DATA
    private lateinit var socket: Socket
    private lateinit var socketDOS: DataOutputStream
    private lateinit var socketDIS: DataInputStream
    private var dataTransferListener: ((
        displayName: String, dataSize: Long, percentTransferred: Float,
        transferState: MediaTransferProtocol.TransferState
    ) -> Unit)? = null
    private val localBroadcastManager = LocalBroadcastManager.getInstance(this)
    private val incomingDataBroadcastIntent =
        Intent(IncomingDataBroadcastReceiver.INCOMING_DATA_BYTES_RECEIVED_ACTION)


    @Inject
    lateinit var mediaTransferProtocol: MediaTransferProtocol

    @Inject
    lateinit var fileTransferServiceNotification: FileTransferServiceNotification

    inner class DataTransferServiceBinder : Binder() {
        fun getServiceInstance(): DataTransferService {
            return this@DataTransferService
        }
    }

    private var dataCollection: MutableList<DataToTransfer> = mutableListOf()
    private val mutex = Mutex()

    fun cancelScheduledTransfer() {

    }

    fun cancelActiveReceive() {
        when (dataTransferUserEvent) {
            DataTransferUserEvent.NO_DATA -> {
                // not transferring any data, but wants to stop receiving data from peer,
                // so send a message to peer to cancel ongoing transfer
                dataTransferUserEvent = DataTransferUserEvent.CANCEL_ON_GOING_TRANSFER
            }
            DataTransferUserEvent.DATA_AVAILABLE -> {
                // transferring data to peer but wants to stop receiving from peer,
                // so send a message to the peer to stop reading for new bytes while I stop sending
                mediaTransferProtocol.cancelCurrentTransfer(
                    transferMetaData =
                    MediaTransferProtocol.TransferMetaData.KEEP_RECEIVING_BUT_CANCEL_ACTIVE_TRANSFER
                )
            }
            DataTransferUserEvent.CANCEL_ON_GOING_TRANSFER -> {

            }
        }
    }

    fun cancelActiveTransfer() {
        mediaTransferProtocol.cancelCurrentTransfer(
            transferMetaData = MediaTransferProtocol.TransferMetaData.CANCEL_ACTIVE_RECEIVE
        )
    }

    @Synchronized
    fun transferData(
        dataCollectionSelected: MutableList<DataToTransfer>,
        dataTransferListener: (
            displayName: String, dataSize: Long, percentTransferred: Float,
            transferState: MediaTransferProtocol.TransferState
        ) -> Unit
    ) {
        while (dataTransferUserEvent == DataTransferUserEvent.DATA_AVAILABLE) {
            // get stuck here
        }
        // when dataTransferUserEvent shows data is not available then assign the new data
        this.dataTransferListener = dataTransferListener
        dataCollection = dataCollectionSelected
        dataTransferUserEvent = DataTransferUserEvent.DATA_AVAILABLE
    }


    override fun onBind(intent: Intent): IBinder {
        return dataTransferService
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(
            FILE_TRANSFER_FOREGROUND_NOTIFICATION_ID,
            fileTransferServiceNotification.configureFileTransferNotification()
        )
        intent?.let {
            when (intent.getBooleanExtra(IS_SERVER, false)) {
                false -> configureClientSocket(intent.getStringExtra(SERVER_IP_ADDRESS)!!)
                true -> configureServerSocket()
            }
        }
        return START_NOT_STICKY
    }


    @Suppress("BlockingMethodInNonBlockingContext")
    private fun configureServerSocket() {
        CoroutineScope(Dispatchers.IO).launch {
            val serverSocket = ServerSocket(8000)
            socket = serverSocket.accept()
            socketDOS = DataOutputStream(BufferedOutputStream(socket.getOutputStream()))
            socketDIS = DataInputStream(BufferedInputStream(socket.getInputStream()))

            launch {
                listenForMediaToTransfer(socketDOS)
            }
            delay(300)
            listenForMediaToReceive(socketDIS)
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private fun configureClientSocket(serverIpAddress: String) {
        CoroutineScope(Dispatchers.IO).launch {
            socket = Socket()
            socket.bind(null)
            socket.connect(InetSocketAddress(serverIpAddress, 9090), 1000)
            socketDOS = DataOutputStream(BufferedOutputStream(socket.getOutputStream()))
            socketDIS = DataInputStream(BufferedInputStream(socket.getInputStream()))
            launch {
                listenForMediaToTransfer(socketDOS)
            }
            delay(300)
            listenForMediaToReceive(socketDIS)
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun listenForMediaToTransfer(dataOutputStream: DataOutputStream) {
        while (true) {
            when (dataTransferUserEvent) {
                DataTransferUserEvent.NO_DATA -> {
                    dataOutputStream.writeInt(dataTransferUserEvent.state.length)
                    dataOutputStream.writeChars(dataTransferUserEvent.state)
                }
                DataTransferUserEvent.DATA_AVAILABLE -> {
                    dataCollection.forEach {
                        dataOutputStream.writeUTF(dataTransferUserEvent.state)
                        mediaTransferProtocol.transferMedia(
                            it,
                            dataOutputStream
                        ) { displayName: String, dataSize: Long, percentTransferred: Float, transferState: MediaTransferProtocol.TransferState ->
                            dataTransferListener?.invoke(
                                displayName,
                                dataSize,
                                percentTransferred,
                                transferState
                            )
                        }
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
    private suspend fun listenForMediaToReceive(dataInputStream: DataInputStream) {
        var messageLength = 0
        val messageReceived = StringBuilder()
        while (true) {
            messageReceived.setLength(0)
            messageLength = dataInputStream.readInt()
            for (i in 0 until messageLength) {
                messageReceived.append(dataInputStream.readChar())
            }
            when (messageReceived.toString()) {
                DataTransferUserEvent.NO_DATA.state -> continue
                DataTransferUserEvent.DATA_AVAILABLE.state -> {
                    //  Log.i("DataAvailable", "Data available to receive")
                    mediaTransferProtocol.receiveMedia(dataInputStream) { dataDisplayName: String, dataSize: Long, percentageOfDataRead: Float, dataType: String, dataUri: Uri ->
                        incomingDataBroadcastIntent.apply {
                            putExtra(
                                IncomingDataBroadcastReceiver.INCOMING_FILE_NAME,
                                dataDisplayName
                            )
                            putExtra(IncomingDataBroadcastReceiver.INCOMING_FILE_URI, dataUri)
                            putExtra(
                                IncomingDataBroadcastReceiver.PERCENTAGE_OF_DATA_RECEIVED,
                                percentageOfDataRead
                            )
                            putExtra(
                                IncomingDataBroadcastReceiver.INCOMING_FILE_MIME_TYPE,
                                dataType
                            )
                            putExtra(IncomingDataBroadcastReceiver.INCOMING_FILE_SIZE, dataSize)
                            localBroadcastManager.sendBroadcast(this)
                        }
                    }
                }
                DataTransferUserEvent.CANCEL_ON_GOING_TRANSFER.state -> {
                    mediaTransferProtocol.cancelCurrentTransfer(
                        transferMetaData = MediaTransferProtocol.TransferMetaData.CANCEL_ACTIVE_RECEIVE
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}