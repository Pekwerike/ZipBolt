package com.salesground.zipbolt.repository

import android.content.pm.ApplicationInfo
import com.salesground.zipbolt.model.ApplicationModel
import com.salesground.zipbolt.model.DataToTransfer

interface ApplicationsRepositoryInterface {
    suspend fun getAllAppsOnDevice(): MutableList<ApplicationInfo>
    suspend fun getNonSystemAppsOnDevice(): List<DataToTransfer>
    suspend fun insertApplicationIntoDevice()
}