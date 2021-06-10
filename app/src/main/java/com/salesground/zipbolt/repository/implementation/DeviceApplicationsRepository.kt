package com.salesground.zipbolt.repository.implementation

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import com.salesground.zipbolt.communication.MediaTransferProtocol
import com.salesground.zipbolt.communication.readStreamDataIntoFile
import com.salesground.zipbolt.model.ApplicationModel
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.repository.ApplicationsRepositoryInterface
import com.salesground.zipbolt.repository.SavedFilesRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.*
import javax.inject.Inject


class DeviceApplicationsRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val savedFilesRepository: SavedFilesRepository
) : ApplicationsRepositoryInterface {
    private val buffer = ByteArray(1024 * 1000)
    private lateinit var appFileBufferedOutputStream : BufferedOutputStream

    private val zipBoltAppsFolder = savedFilesRepository.getZipBoltMediaCategoryBaseDirectory(
        SavedFilesRepository.ZipBoltMediaCategory.APPS_BASE_DIRECTORY
    )

    override suspend fun getAllAppsOnDevice(): MutableList<ApplicationInfo> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.packageManager.getInstalledApplications(PackageManager.INSTALL_REASON_USER)
        } else {
            context.packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        }
    }

    override suspend fun getNonSystemAppsOnDevice(): List<DataToTransfer> {
        return getAllAppsOnDevice().filter {
            context.packageManager.getLaunchIntentForPackage(it.packageName) != null
        }.map {
            DataToTransfer.DeviceApplication(
                applicationName = it.loadLabel(context.packageManager).toString(),
                apkPath = it.sourceDir,
                appIcon = it.loadIcon(context.packageManager),
                appSize = File(it.sourceDir).length()
            )
        }.sortedBy {
            it.applicationName
        }
    }

    override suspend fun insertApplicationIntoDevice(
        appFileName: String,
        appSize: Long,
        dataInputStream: DataInputStream,
        transferMetaDataUpdateListener: MediaTransferProtocol.TransferMetaDataUpdateListener,
        dataReceiveListener: MediaTransferProtocol.DataReceiveListener
    ) {
        val applicationFile = File(zipBoltAppsFolder, appFileName)
        appFileBufferedOutputStream = BufferedOutputStream(FileOutputStream(applicationFile))
        val applicationFileUri = Uri.fromFile(applicationFile)
        // percentage of bytes read is 0%
        dataReceiveListener.onReceive(
            appFileName,
            appSize,
            0f,
            DataToTransfer.MediaType.APP.value,
            applicationFileUri,
            DataToTransfer.TransferStatus.RECEIVE_STARTED
        )

        dataInputStream.readStreamDataIntoFile(
            dataReceiveListener,
            appFileName,
            appSize,
            transferMetaDataUpdateListener,
            applicationFile,

        )

    }
}