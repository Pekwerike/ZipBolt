package com.salesground.zipbolt.repository.implementation

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import com.salesground.zipbolt.communication.MediaTransferProtocol
import com.salesground.zipbolt.communication.readStreamDataIntoFile
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.model.MediaType
import com.salesground.zipbolt.repository.ApplicationsRepositoryInterface
import com.salesground.zipbolt.repository.SavedFilesRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.*
import javax.inject.Inject


class DeviceApplicationsRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    savedFilesRepository: SavedFilesRepository
) : ApplicationsRepositoryInterface {

    private val zipBoltAppsFolder = savedFilesRepository.getZipBoltMediaCategoryBaseDirectory(
        SavedFilesRepository.ZipBoltMediaCategory.APPS_BASE_DIRECTORY
    )

    override suspend fun getAllAppsOnDevice(): MutableList<ApplicationInfo> {
        // val applicationInfoList = mutableListOf<ApplicationInfo>()
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.packageManager
                .getInstalledApplications(PackageManager.INSTALL_REASON_USER)/*.forEach {
                    if ((it.flags and ApplicationInfo.FLAG_SYSTEM) != ApplicationInfo.FLAG_SYSTEM) {
                        applicationInfoList.add(it)
                    }
                }*/

        } else {
            context.packageManager.getInstalledApplications(PackageManager.GET_META_DATA)/*.forEach {
                if ((it.flags and ApplicationInfo.FLAG_SYSTEM) != ApplicationInfo.FLAG_SYSTEM) {
                    applicationInfoList.add(it)
                }
            }*/
        }
        //return applicationInfoList
    }

    override suspend fun getNonSystemAppsOnDevice(): List<DataToTransfer> {
        return getAllAppsOnDevice().filter {
            context.packageManager.getLaunchIntentForPackage(it.packageName) != null
        }.map {
            DataToTransfer.DeviceApplication(
                applicationName = it.loadLabel(context.packageManager).toString(),
                apkPath = it.sourceDir,
                appSize = File(it.sourceDir).length(),
                applicationIcon = it.loadIcon(context.packageManager)
            )
        }.sortedBy {
            it.applicationName
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun insertApplicationIntoDevice(
        appFileName: String,
        appSize: Long,
        dataInputStream: DataInputStream,
        transferMetaDataUpdateListener: MediaTransferProtocol.TransferMetaDataUpdateListener,
        dataReceiveListener: MediaTransferProtocol.DataReceiveListener
    ) {
        val applicationFile = File(zipBoltAppsFolder, "$appFileName.apk")
        val applicationFileUri = Uri.fromFile(applicationFile)

        // percentage of bytes read is 0%
        dataReceiveListener.onReceive(
            appFileName,
            appSize,
            0f,
            MediaType.App.value,
            applicationFileUri,
            DataToTransfer.TransferStatus.RECEIVE_STARTED
        )

        if (!dataInputStream.readStreamDataIntoFile(
                dataReceiveListener,
                appFileName,
                appSize,
                transferMetaDataUpdateListener,
                applicationFile,
                MediaType.App
            )
        ) {
            // application receive was cancelled
            return
        }

        dataReceiveListener.onReceive(
            appFileName,
            appSize, 100f,
            MediaType.App.value,
            applicationFileUri,
            DataToTransfer.TransferStatus.RECEIVE_COMPLETE
        )
    }
}