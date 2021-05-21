package com.salesground.zipbolt.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.salesground.zipbolt.MainActivity
import com.salesground.zipbolt.OPEN_MAIN_ACTIVITY_PENDING_INTENT_REQUEST_CODE
import com.salesground.zipbolt.R
import com.salesground.zipbolt.communication.DataTransferUtils
import com.salesground.zipbolt.broadcast.IncomingDataBroadcastReceiver
import com.salesground.zipbolt.communication.MediaTransferProtocol
import com.salesground.zipbolt.communication.MediaTransferProtocol.*
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.notification.FILE_TRANSFER_SERVICE_NOTIFICATION_ID
import com.salesground.zipbolt.notification.FileTransferServiceNotification
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketAddress
import java.util.Queue
import javax.inject.Inject

@AndroidEntryPoint
class DataTransferService : Service() {
    var isActive = false

    companion object {
        const val IS_SERVER: String = "IsDeviceTheServer"
        const val SERVER_IP_ADDRESS = "ServerIpAddress"
    }

    private val dataTransferService: DataTransferServiceBinder = DataTransferServiceBinder()

    // private var dataTransferUserEvent = DataTransferUserEvent.NO_DATA
    private var mediaTransferProtocolMetaData =
        MediaTransferProtocolMetaData.NO_DATA
    private lateinit var socket: Socket
    private lateinit var socketDOS: DataOutputStream
    private lateinit var socketDIS: DataInputStream
    private var dataTransferListener: ((
        displayName: String, dataSize: Long, percentTransferred: Float,
        transferState: TransferState
    ) -> Unit)? = null
    private val incomingDataBroadcastIntent =
        Intent(IncomingDataBroadcastReceiver.INCOMING_DATA_BYTES_RECEIVED_ACTION)


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


    fun cancelScheduledTransfer() {

    }

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
            MediaTransferProtocolMetaData.CANCEL_ACTIVE_TRANSTER -> {

            }
            MediaTransferProtocolMetaData.KEEP_RECEIVING -> {

            }
            MediaTransferProtocolMetaData.KEEP_RECEIVING_BUT_CANCEL_ACTIVE_TRANSFER -> {

            }
            MediaTransferProtocolMetaData.PAUSE_ACTIVE_TRANSFER -> {

            }
        }
        /*  when (dataTransferUserEvent) {
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
                      TransferMetaData.KEEP_RECEIVING_BUT_CANCEL_ACTIVE_TRANSFER
                  )
              }
              DataTransferUserEvent.CANCEL_ON_GOING_TRANSFER -> {

              }
          }*/
    }

    fun cancelActiveTransfer() {
        mediaTransferProtocol.cancelCurrentTransfer(
            transferMetaData = MediaTransferProtocolMetaData.CANCEL_ACTIVE_RECEIVE
        )
    }

    @Synchronized
    fun transferData(
        dataCollectionSelected: MutableList<DataToTransfer>,
        dataTransferListener: (
            displayName: String, dataSize: Long, percentTransferred: Float,
            transferState: TransferState
        ) -> Unit
    ) {
        while (mediaTransferProtocolMetaData == MediaTransferProtocolMetaData.DATA_AVAILABLE) {
            // get stuck here
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


    @Suppress("BlockingMethodInNonBlockingContext")
    private fun configureServerSocket() {
        CoroutineScope(Dispatchers.IO).launch {
            val serverSocket = ServerSocket()
            serverSocket.reuseAddress = true
            serverSocket.bind(InetSocketAddress(SOCKET_PORT))
            socket = serverSocket.accept()
            socketDOS = DataOutputStream(BufferedOutputStream(socket.getOutputStream()))
            socketDIS = DataInputStream(BufferedInputStream(socket.getInputStream()))

            launch(Dispatchers.IO) {
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
            socket.connect(InetSocketAddress(serverIpAddress, SOCKET_PORT), 100000)
            socketDOS = DataOutputStream(BufferedOutputStream(socket.getOutputStream()))
            socketDIS = DataInputStream(BufferedInputStream(socket.getInputStream()))
            launch(Dispatchers.IO) {
                listenForMediaToTransfer(socketDOS)
            }
            delay(300)
            listenForMediaToReceive(socketDIS)
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun listenForMediaToTransfer(dataOutputStream: DataOutputStream) {
        while (true) {
            when (mediaTransferProtocolMetaData) {
                MediaTransferProtocolMetaData.NO_DATA -> {
                    dataOutputStream.writeInt(mediaTransferProtocolMetaData.value)
                }
                MediaTransferProtocolMetaData.DATA_AVAILABLE -> {
                    for (dataToTransfer in dataCollection) {
                        dataOutputStream.writeInt(mediaTransferProtocolMetaData.value)
                        mediaTransferProtocol.transferMedia(
                            dataToTransfer,
                            dataOutputStream
                        ) { displayName: String, dataSize: Long, percentTransferred: Float, transferState: TransferState ->
                            dataTransferListener?.invoke(
                                displayName,
                                dataSize,
                                percentTransferred,
                                transferState
                            )
                        }
                        delay(100)
                    }
                    mediaTransferProtocolMetaData = MediaTransferProtocolMetaData.NO_DATA
                    //   dataOutputStream.writeInt(mediaTransferProtocolMetaData.value)
                }
                MediaTransferProtocolMetaData.CANCEL_ON_GOING_TRANSFER -> {
                    mediaTransferProtocol.cancelCurrentTransfer(
                        transferMetaData =
                        MediaTransferProtocolMetaData.CANCEL_ACTIVE_RECEIVE
                    )
                }
                else -> {

                }
            }
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun listenForMediaToReceive(dataInputStream: DataInputStream) {
        while (true) {
            when (dataInputStream.readInt()) {
                MediaTransferProtocolMetaData.NO_DATA.value -> continue
                MediaTransferProtocolMetaData.DATA_AVAILABLE.value -> {
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
                    delay(100)
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