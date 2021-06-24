package com.salesground.zipbolt.communication.implementation

import android.content.Context
import android.net.Uri
import com.salesground.zipbolt.communication.MediaTransferProtocol
import com.salesground.zipbolt.communication.MediaTransferProtocol.*
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.repository.*
import com.salesground.zipbolt.repository.implementation.AdvanceImageRepository
import com.salesground.zipbolt.repository.implementation.DeviceApplicationsRepository
import com.salesground.zipbolt.repository.implementation.ZipBoltVideoRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.*
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import kotlin.math.min

open class MediaTransferProtocolImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : MediaTransferProtocol {

    private val savedFilesRepository: SavedFilesRepository by lazy {
        ZipBoltSavedFilesRepository()
    }

    private val imageRepository: ImageRepository by lazy {
        AdvanceImageRepository(
            context,
            savedFilesRepository
        )
    }

    private val applicationsRepository: ApplicationsRepositoryInterface by lazy {
        DeviceApplicationsRepository(
            context,
            savedFilesRepository
        )
    }

    private val videoRepository: VideoRepositoryI by lazy {
        ZipBoltVideoRepository(
            savedFilesRepository,
            context
        )
    }

    private val transferMetaDataUpdateListener: TransferMetaDataUpdateListener by lazy {
        object : TransferMetaDataUpdateListener {
            override fun onMetaTransferDataUpdate(mediaTransferProtocolMetaData: MediaTransferProtocolMetaData) {
                if (mediaTransferProtocolMetaData == MediaTransferProtocolMetaData.KEEP_RECEIVING_BUT_CANCEL_ACTIVE_TRANSFER) {
                    cancelCurrentTransfer(MediaTransferProtocolMetaData.CANCEL_ACTIVE_RECEIVE)
                }
            }
        }
    }
    private var mTransferMetaData = MediaTransferProtocolMetaData.KEEP_RECEIVING
    private var ongoingTransfer = AtomicBoolean(false)
    private val buffer = ByteArray(1024 * 1024)
    private var dataToTransfer: DataToTransfer? = null

    // data receive variables
    private var mediaType: Int = 0
    private var mediaName: String = ""
    private var mediaSize: Long = 0L


    override fun cancelCurrentTransfer(transferMetaData: MediaTransferProtocolMetaData) {
        if (ongoingTransfer.get()) mTransferMetaData = transferMetaData
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun transferMedia(
        dataToTransfer: DataToTransfer,
        dataOutputStream: DataOutputStream,
        dataTransferListener: DataTransferListener
    ) {
        ongoingTransfer.set(true)
        this.dataToTransfer = dataToTransfer
        dataOutputStream.writeInt(dataToTransfer.dataType)
        dataOutputStream.writeUTF(dataToTransfer.dataDisplayName)
        dataOutputStream.writeLong(dataToTransfer.dataSize)

        // write the video duration if dataToTransfer is a video
        if (dataToTransfer.dataType == DataToTransfer.MediaType.VIDEO.value) {
            dataOutputStream.writeLong(dataToTransfer.videoDuration)
        }

        val fileInputStream: InputStream? =
            if (dataToTransfer.dataType == DataToTransfer.MediaType.IMAGE.value
                || dataToTransfer.dataType == DataToTransfer.MediaType.VIDEO.value
            ) {
                context.contentResolver.openInputStream(dataToTransfer.dataUri)
            } else {
                dataToTransfer as DataToTransfer.DeviceApplication
                FileInputStream(dataToTransfer.apkPath)
            }

        fileInputStream?.let {
            val fileDataInputStream = DataInputStream(
                BufferedInputStream(
                    it
                )
            )

            dataTransferListener.onTransfer(
                this.dataToTransfer!!,
                0f,
                DataToTransfer.TransferStatus.TRANSFER_STARTED
            )

            var lengthRead: Int
            var lengthUnread = dataToTransfer.dataSize
            fileDataInputStream.readFully(
                buffer, 0, min(lengthUnread, buffer.size.toLong())
                    .toInt()
            ).also {
                lengthRead = min(lengthUnread, buffer.size.toLong())
                    .toInt()
                lengthUnread -= lengthRead
                dataOutputStream.writeInt(mTransferMetaData.value)
                dataOutputStream.write(buffer, 0, lengthRead)
            }

            while (lengthUnread > 0) {
                fileDataInputStream.readFully(
                    buffer, 0,
                    min(lengthUnread, buffer.size.toLong()).toInt()
                ).also {
                    lengthRead = min(lengthUnread, buffer.size.toLong()).toInt()
                    lengthUnread -= lengthRead
                }
                dataOutputStream.writeInt(mTransferMetaData.value)

                if (mTransferMetaData == MediaTransferProtocolMetaData.CANCEL_ACTIVE_RECEIVE) {
                    break
                } else if (mTransferMetaData == MediaTransferProtocolMetaData.KEEP_RECEIVING_BUT_CANCEL_ACTIVE_TRANSFER) {
                    mTransferMetaData = MediaTransferProtocolMetaData.KEEP_RECEIVING
                }

                dataOutputStream.write(buffer, 0, lengthRead)

                dataTransferListener.onTransfer(
                    this.dataToTransfer!!,
                    ((dataToTransfer.dataSize - lengthUnread) / dataToTransfer.dataSize.toFloat()) * 100f,
                    DataToTransfer.TransferStatus.TRANSFER_ONGOING
                )
            }
            // set ongoing transfer to false, so that a user cannot attempt to cancel it
            ongoingTransfer.set(false)

            // only send a 100% transfer event, when the data transfer was not cancelled
            if (mTransferMetaData != MediaTransferProtocolMetaData.CANCEL_ACTIVE_RECEIVE) {
                dataTransferListener.onTransfer(
                    this.dataToTransfer!!,
                    100f,
                    DataToTransfer.TransferStatus.TRANSFER_COMPLETE
                )
            } else {
                // send event that transfer has been cancelled
                dataTransferListener.onTransfer(
                    this.dataToTransfer!!,
                    -1f,
                    DataToTransfer.TransferStatus.TRANSFER_CANCELLED
                )
            }

            fileDataInputStream.close()
            mTransferMetaData = MediaTransferProtocolMetaData.KEEP_RECEIVING

        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun receiveMedia(
        dataInputStream: DataInputStream,
        dataReceiveListener: DataReceiveListener
    ) {
        mediaType = dataInputStream.readInt()
        mediaName = dataInputStream.readUTF()
        mediaSize = dataInputStream.readLong()



        when (mediaType) {
            DataToTransfer.MediaType.IMAGE.value -> {
                imageRepository.insertImageIntoMediaStore(
                    displayName = mediaName,
                    size = mediaSize,
                    dataInputStream = dataInputStream,
                    transferMetaDataUpdateListener = transferMetaDataUpdateListener,
                    dataReceiveListener = dataReceiveListener
                )
            }
            DataToTransfer.MediaType.APP.value -> {
                applicationsRepository.insertApplicationIntoDevice(
                    appFileName = mediaName,
                    appSize = mediaSize,
                    dataInputStream = dataInputStream,
                    transferMetaDataUpdateListener = transferMetaDataUpdateListener,
                    dataReceiveListener = dataReceiveListener
                )
            }

            DataToTransfer.MediaType.VIDEO.value -> {
                videoRepository.insertVideoIntoMediaStore(
                    videoName = mediaName,
                    videoSize = mediaSize,
                    videoDuration = dataInputStream.readLong(),
                    dataInputStream = dataInputStream,
                    transferMetaDataUpdateListener = transferMetaDataUpdateListener,
                    dataReceiveListener = dataReceiveListener
                )
            }
        }
    }

}
