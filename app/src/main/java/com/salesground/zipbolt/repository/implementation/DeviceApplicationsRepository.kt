package com.salesground.zipbolt.repository.implementation

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import com.salesground.zipbolt.model.ApplicationModel
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.repository.ApplicationsRepositoryInterface
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import javax.inject.Inject


class DeviceApplicationsRepository @Inject constructor(@ApplicationContext private val context: Context) :
    ApplicationsRepositoryInterface {

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

    override suspend fun insertApplicationIntoDevice() {

    }
}