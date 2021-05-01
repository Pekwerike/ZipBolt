package com.salesground.zipbolt

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import javax.inject.Inject
import javax.inject.Singleton

const val READ_WRITE_STORAGE_REQUEST_CODE = 101

@Singleton
class PermissionUtils @Inject constructor(
    private val mainActivity: MainActivity
) {

    fun checkReadAndWriteExternalStoragePermission() {
        if (ActivityCompat.checkSelfPermission(
                mainActivity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(
                mainActivity,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            // Permission granted to SpeedForce to read and write to the device external storage
            // TODO Go ahead an inform the viewModel to fetch, media items from the repositoris
        } else {
            ActivityCompat.requestPermissions(
                mainActivity,
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                READ_WRITE_STORAGE_REQUEST_CODE
            )
        }
    }
}