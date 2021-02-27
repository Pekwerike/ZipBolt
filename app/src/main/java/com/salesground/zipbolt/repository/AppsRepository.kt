package com.salesground.zipbolt.repository

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build


class AppsRepository(private val context: Context) {


    fun getAllAppsOnDevice(): MutableList<ApplicationInfo> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.packageManager.getInstalledApplications(PackageManager.INSTALL_REASON_USER)
        }else {
            context.packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        }
    }

    fun getTheRealFileOfAppsOnDevice(){
        val allAppsOnDevice = getAllAppsOnDevice()
        allAppsOnDevice.forEach {

        }
    }

}