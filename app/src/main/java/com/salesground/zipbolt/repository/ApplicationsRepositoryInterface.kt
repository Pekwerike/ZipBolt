package com.salesground.zipbolt.repository

import android.content.pm.ApplicationInfo
import com.salesground.zipbolt.model.ApplicationModel

interface ApplicationsRepositoryInterface {
    fun getAllAppsOnDevice(): MutableList<ApplicationInfo>
    fun getNonSystemAppsOnDevice(): List<ApplicationModel>
}