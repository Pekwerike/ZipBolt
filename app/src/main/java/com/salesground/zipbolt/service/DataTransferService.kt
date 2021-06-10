package com.salesground.zipbolt.service

import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.salesground.zipbolt.broadcast.DataTransferServiceConnectionStateReceiver
import com.salesground.zipbolt.communication.MediaTransferProtocol
import com.salesground.zipbolt.communication.MediaTransferProtocol.*
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.notification.FileTransferServiceNotification
import com.salesground.zipbolt.notification.FileTransferServiceNotification.Companion.FILE_TRANSFER_FOREGROUND_NOTIFICATION_ID
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.ConnectException
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import javax.inject.Inject

@AndroidEntryPoint
class DataTransferService : Service() {
    var isActive = false

    companion object {
        const val IS_SERVER: String = "IsDeviceTheServer"
        const val SERVER_IP_ADDRESS = "ServerIpAddress"
        const val SOCKET_PORT = 7091
    }

    private val dataTransferService: DataTransferServiceBinder = DataTransferServiceBinder()

    // private var dataTransferUserEvent = DataTransferUserEvent.NO_DATA
    private var mediaTransferProtocolMetaData =
        MediaTransferProtocolMetaData.NO_DATA
    private lateinit var socket: Socket
    private lateinit var socketDOS: DataOutputStream
    private lateinit var socketDIS: DataInputStream
    private var dataTransferListener: ((
        dataToTransfer: DataToTransfer,
        percentTransferred: Float,
        transferStatus: DataToTransfer.TransferStatus
    ) -> Unit)? = null
    private val incomingDataBroadcastIntent = Intent()
    private val dataTransferServiceConnectionStateIntent = Intent()

    @Inject
    lateinit var localBroadcastManager: LocalBroadcastManager

    @Inject
    lateinit var mediaTransferProtocol: MediaTransferProtocol

    @Inject
    lateinit var fileTransferServiceNotification: FileTransferServiceNotification

    inner class DataTransferServiceBinder : Binder() {
        fun getServiceInstance(): DataTransferService {
            return this@DataTransferService
        }
    }

    // variables, interfaces and functions for dataFlowListener
    private var dataFlowListener: DataFlowListener? = null

    fun setOnDataReceiveListener(dataFlowListener: DataFlowListener) {
        this.dataFlowListener = dataFlowListener
    }

    private val mediaTransferProtocolDataTransferListener: DataTransferListener by lazy {
        object : DataTransferListener {
            override fun onTransfer(
                dataToTransfer: DataToTransfer,
                percentTransferred: Float,
                transferStatus: DataToTransfer.TransferStatus
            ) {
                dataFlowListener?.onDataTransfer(
                    dataToTransfer,
                    percentTransferred,
                    transferStatus
                )
            }
        }

    }

    private val mediaTransferProtocolDataReceiveListener: DataReceiveListener by lazy {
        object : DataReceiveListener {
            override fun onReceive(
                dataDisplayName: String,
                dataSize: Long,
                percentageOfDataRead: Float,
                dataType: Int,
                dataUri: Uri?,
                dataTransferStatus: DataToTransfer.TransferStatus
            ) {
                dataFlowListener?.onDataReceive(
                    dataDisplayName,
                    dataSize,
                    percentageOfDataRead,
                    dataType,
                    dataUri,
                    dataTransferStatus
                )
            }
        }
    }

    interface DataFlowListener {
        fun onDataTransfer(
            dataToTransfer: DataToTransfer,
            percentTransferred: Float,
            transferStatus: DataToTransfer.TransferStatus
        )

        fun onDataReceive(
            dataDisplayName: String,
            dataSize: Long,
            percentageOfDataRead: Float,
            dataType: Int,
            dataUri: Uri?,
            dataTransferStatus: DataToTransfer.TransferStatus
        )

        fun totalFileReceiveComplete()

    }

    private var dataCollection: MutableList<DataToTransfer> = mutableListOf()

    fun cancelActiveReceive() {
        when (mediaTransferProtocolMetaData) {
            MediaTransferProtocolMetaData.NO_DATA -> {
                // not transferring any data, but wants to stop receiving data from peer,
                // so send a message to peer to cancel ongoing transfer
                mediaTransferProtocolMetaData =
                    MediaTransferProtocolMetaData.CANCEL_ON_GOING_TRANSFER
            }
            MediaTransferProtocolMetaData.DATA_AVAILABLE -> {
                // transferring data to peer but wants to stop receiving from peer,
                // so send a message to the peer to stop reading for new bytes while I stop sending
                mediaTransferProtocol.cancelCurrentTransfer(
                    transferMetaData =
                    MediaTransferProtocolMetaData.KEEP_RECEIVING_BUT_CANCEL_ACTIVE_TRANSFER
                )
            }
            MediaTransferProtocolMetaData.CANCEL_ON_GOING_TRANSFER -> {

            }
            MediaTransferProtocolMetaData.CANCEL_ACTIVE_RECEIVE -> {

            }
            MediaTransferProtocolMetaData.CANCEL_ACTIVE_TRANSFER -> {

            }
            MediaTransferProtocolMetaData.KEEP_RECEIVING -> {

            }
            MediaTransferProtocolMetaData.KEEP_RECEIVING_BUT_CANCEL_ACTIVE_TRANSFER -> {

            }
            MediaTransferProtocolMetaData.PAUSE_ACTIVE_TRANSFER -> {

            }
        }
    }

    fun cancelActiveTransfer() {
        mediaTransferProtocol.cancelCurrentTransfer(
            transferMetaData = MediaTransferProtocolMetaData.CANCEL_ACTIVE_RECEIVE
        )

    }

    fun transferData(dataCollectionSelected: MutableList<DataToTransfer>) {
        while (mediaTransferProtocolMetaData == MediaTransferProtocolMetaData.DATA_AVAILABLE) {
            // get stuck here
            return
        }
        // when dataTransferUserEvent shows data is not available then assign the new data
        dataCollection = dataCollectionSelected
        mediaTransferProtocolMetaData = MediaTransferProtocolMetaData.DATA_AVAILABLE
    }


    override fun onBind(intent: Intent): IBinder {
        return dataTransferService
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        isActive = true
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


    private fun configureServerSocket() {
        CoroutineScope(Dispatchers.IO).launch {
            val serverSocket = withContext(Dispatchers.IO) { ServerSocket() }
            serverSocket.reuseAddress = true
            withContext(Dispatchers.IO) { serverSocket.bind(InetSocketAddress(SOCKET_PORT)) }
            socket = withContext(Dispatchers.IO) { serverSocket.accept() }
            socketDOS =
                DataOutputStream(BufferedOutputStream(withContext(Dispatchers.IO) { socket.getOutputStream() }))
            socketDIS =
                DataInputStream(BufferedInputStream(withContext(Dispatchers.IO) { socket.getInputStream() }))

            launch {
                listenForMediaToTransfer(socketDOS)
            }
            delay(300)
            listenForMediaToReceive(socketDIS)
        }
    }


    private fun configureClientSocket(serverIpAddress: String) {
        CoroutineScope(Dispatchers.IO).launch {
            socket = Socket()
            socket.bind(null)
            try {
                socket.connect(
                    InetSocketAddress(
                        serverIpAddress,
                        SOCKET_PORT
                    ), 100000
                )
            } catch (connectException: ConnectException) {
                // send broadcast message to the main activity that we couldn't connect to peer.
                // the main activity will use this message to determine how to update the ui
                with(dataTransferServiceConnectionStateIntent) {
                    action =
                        DataTransferServiceConnectionStateReceiver.ACTION_CANNOT_CONNECT_TO_PEER_ADDRESS

                    localBroadcastManager.sendBroadcast(this)
                }
                stopForeground(true)
                stopSelf()
            }

            socketDOS =
                DataOutputStream(BufferedOutputStream(withContext(Dispatchers.IO) { socket.getOutputStream() }))
            socketDIS =
                DataInputStream(BufferedInputStream(withContext(Dispatchers.IO) { socket.getInputStream() }))
            launch {
                listenForMediaToTransfer(socketDOS)
            }
            delay(300)
            listenForMediaToReceive(socketDIS)
        }
    }


    private suspend fun listenForMediaToTransfer(dataOutputStream: DataOutputStream) {
        try {
            while (true) {
                when (mediaTransferProtocolMetaData) {
                    MediaTransferProtocolMetaData.NO_DATA -> {
                        dataOutputStream.writeInt(mediaTransferProtocolMetaData.value)
                    }
                    MediaTransferProtocolMetaData.DATA_AVAILABLE -> {
                        // write the collection size to the peer
                        dataOutputStream.writeInt(mediaTransferProtocolMetaData.value)
                        dataOutputStream.writeInt(dataCollection.size)
                        for (dataToTransfer in dataCollection) {
                            mediaTransferProtocol.transferMedia(
                                dataToTransfer,
                                dataOutputStream,
                                mediaTransferProtocolDataTransferListener
                            )
                        }
                        dataOutputStream.flush()
                        mediaTransferProtocolMetaData = MediaTransferProtocolMetaData.NO_DATA
                    }
                    MediaTransferProtocolMetaData.CANCEL_ON_GOING_TRANSFER -> {
                        dataOutputStream.writeInt(mediaTransferProtocolMetaData.value)
                        mediaTransferProtocolMetaData = MediaTransferProtocolMetaData.NO_DATA
                    }
                    else -> {

                    }
                }
            }
        } catch (exception: Exception) {
            // send broadcast message to the main activity that we couldn't connect to peer.
            // the main activity will use this message to determine how to update the ui
            with(dataTransferServiceConnectionStateIntent) {
                action =
                    DataTransferServiceConnectionStateReceiver.ACTION_DISCONNECTED_FROM_PEER

                localBroadcastManager.sendBroadcast(this)
            }
            stopForeground(true)
            stopSelf()
        }
    }

    private suspend fun listenForMediaToReceive(dataInputStream: DataInputStream) {
        try {
            while (true) {
                when (dataInputStream.readInt()) {
                    MediaTransferProtocolMetaData.NO_DATA.value -> continue
                    MediaTransferProtocolMetaData.DATA_AVAILABLE.value -> {
                        delay(200)
                        // read the number of files sent from the peer
                        val filesCount = dataInputStream.readInt()
                        for (i in 0 until filesCount) {
                            mediaTransferProtocol.receiveMedia(
                                dataInputStream,
                                mediaTransferProtocolDataReceiveListener
                            )
                            delay(200)
                        }
                        dataFlowListener?.totalFileReceiveComplete()
                    }
                    MediaTransferProtocolMetaData.CANCEL_ON_GOING_TRANSFER.value -> {
                        mediaTransferProtocol.cancelCurrentTransfer(
                            transferMetaData = MediaTransferProtocolMetaData.CANCEL_ACTIVE_RECEIVE
                        )
                    }
                }
            }
        } catch (exception: Exception) {
            // send broadcast message to the main activity that we couldn't connect to peer.
            // the main activity will use this message to determine how to update the ui
            with(dataTransferServiceConnectionStateIntent) {
                action =
                    DataTransferServiceConnectionStateReceiver.ACTION_DISCONNECTED_FROM_PEER

                localBroadcastManager.sendBroadcast(this)
            }

            stopForeground(true)
            stopSelf()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}