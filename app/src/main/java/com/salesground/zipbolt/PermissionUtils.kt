package com.salesground.zipbolt

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

const val READ_WRITE_STORAGE_REQUEST_CODE = 101

object PermissionUtils : LifecycleObserver{

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun checkReadAndWriteExternalStoragePermission(activity: Activity) {
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            // Permission granted to SpeedForce to read and write to the device external storage
            // TODO Go ahead an inform the viewModel to fetch, media items from the repositoris
        } else {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                READ_WRITE_STORAGE_REQUEST_CODE
            )
        }
    }
}