package com.salesground.zipbolt.repository

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build


public class AppsRepository(private val context : Context){


    fun getAllAppsOnDevice(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.packageManager.getInstalledApplications(PackageManager.INSTALL_REASON_USER)
        }
    }
}