package com.salesground.zipbolt

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent



object PermissionUtils : LifecycleObserver {

    const val READ_WRITE_STORAGE_REQUEST_CODE = 101


}