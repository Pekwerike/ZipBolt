package com.salesground.zipbolt.service

import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.salesground.zipbolt.broadcast.IncomingDataBroadcastReceiver
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

    fun transferData(
        dataCollectionSelected: MutableList<DataToTransfer>,
        dataTransferListener: (
            dataToTransfer: DataToTransfer,
            percentTransferred: Float,
            transferStatus: DataToTransfer.TransferStatus
        ) -> Unit
    ) {
        while (mediaTransferProtocolMetaData == MediaTransferProtocolMetaData.DATA_AVAILABLE) {
            // get stuck here
            return
        }
        // when dataTransferUserEvent shows data is not available then assign the new data
        this.dataTransferListener = dataTransferListener
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
            withContext(Dispatchers.IO) { socket.bind(null) }
            withContext(Dispatchers.IO) {
                socket.connect(
                    InetSocketAddress(
                        serverIpAddress,
                        SOCKET_PORT
                    ), 100000
                )
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
                            dataOutputStream
                        ) { dataToTransfer: DataToTransfer,
                            percentTransferred: Float,
                            transferStatus: DataToTransfer.TransferStatus ->
                            dataTransferListener?.invoke(
                                dataToTransfer,
                                percentTransferred,
                                transferStatus
                            )
                        }
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
    }

    private suspend fun listenForMediaToReceive(dataInputStream: DataInputStream) {
        while (true) {
            when (dataInputStream.readInt()) {
                MediaTransferProtocolMetaData.NO_DATA.value -> continue
                MediaTransferProtocolMetaData.DATA_AVAILABLE.value -> {
                    delay(200)
                    // read the number of files sent from the peer
                    val filesCount = dataInputStream.readInt()
                    for (i in 0 until filesCount) {
                        mediaTransferProtocol.receiveMedia(dataInputStream) { dataDisplayName: String, dataSize: Long, percentageOfDataRead: Float, dataType: Int, dataUri: Uri?, dataTransferStatus: DataToTransfer.TransferStatus ->
                            with(incomingDataBroadcastIntent) {
                                action =
                                    IncomingDataBroadcastReceiver.INCOMING_DATA_BYTES_RECEIVED_ACTION
                                putExtra(
                                    IncomingDataBroadcastReceiver.INCOMING_FILE_NAME,
                                    dataDisplayName
                                )
                                putExtra(
                                    IncomingDataBroadcastReceiver.INCOMING_FILE_URI,
                                    dataUri
                                )
                                putExtra(
                                    IncomingDataBroadcastReceiver.PERCENTAGE_OF_DATA_RECEIVED,
                                    percentageOfDataRead
                                )
                                putExtra(
                                    IncomingDataBroadcastReceiver.INCOMING_FILE_MIME_TYPE,
                                    dataType
                                )
                                putExtra(
                                    IncomingDataBroadcastReceiver.INCOMING_FILE_SIZE,
                                    dataSize
                                )
                                putExtra(
                                    IncomingDataBroadcastReceiver.INCOMING_FILE_TRANSFER_STATUS,
                                    dataTransferStatus.value
                                )
                                localBroadcastManager.sendBroadcast(this)
                            }
                        }
                        delay(200)
                    }
                    with(incomingDataBroadcastIntent) {
                        action = IncomingDataBroadcastReceiver.ACTION_TOTAL_FILE_RECEIVE_COMPLETE
                        localBroadcastManager.sendBroadcast(this)
                    }
                }
                MediaTransferProtocolMetaData.CANCEL_ON_GOING_TRANSFER.value -> {
                    mediaTransferProtocol.cancelCurrentTransfer(
                        transferMetaData = MediaTransferProtocolMetaData.CANCEL_ACTIVE_RECEIVE
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}