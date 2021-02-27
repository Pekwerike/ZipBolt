package com.salesground.zipbolt.repository

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build


class AppsRepository(private val context: Context) {


    fun getAllAppsOnDevice(): MutableList<ApplicationInfo> {
        return context.packageManager.getInstalledApplications(0)
    }

}