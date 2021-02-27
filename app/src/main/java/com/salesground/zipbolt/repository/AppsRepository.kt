package com.salesground.zipbolt.repository

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import com.salesground.zipbolt.model.ApplicationModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File


class AppsRepository(private val context: Context) {


    fun getAllAppsOnDevice(): MutableList<ApplicationInfo> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.packageManager.getInstalledApplications(PackageManager.INSTALL_REASON_USER)
        } else {
            context.packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        }
    }

    fun getAllApplicationAsCustomModel(): Flow<ApplicationModel> = flow {
        val allAppsOnDevice = getAllAppsOnDevice()
        allAppsOnDevice.map {
            emit(
                ApplicationModel(
                    applicationName = it.name,
                    apkPath = it.sourceDir,
                    appIcon = it.loadIcon(context.packageManager),
                    appSize = File(it.sourceDir).length()
                )
            )
        }
    }

}