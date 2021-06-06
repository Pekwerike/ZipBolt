package com.salesground.zipbolt.repository

import android.content.pm.ApplicationInfo
import com.salesground.zipbolt.model.ApplicationModel
import com.salesground.zipbolt.model.DataToTransfer

interface ApplicationsRepositoryInterface {
    fun getAllAppsOnDevice(): MutableList<ApplicationInfo>
    fun getNonSystemAppsOnDevice(): List<DataToTransfer>
}