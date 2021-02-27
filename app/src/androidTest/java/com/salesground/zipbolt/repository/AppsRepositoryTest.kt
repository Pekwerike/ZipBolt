package com.salesground.zipbolt.repository

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test

class AppsRepositoryTest {

    private val applicationContext = ApplicationProvider.getApplicationContext<Context>()
    private lateinit var appsRepository: AppsRepository

    @Before
    fun setUp() {
        appsRepository = AppsRepository(applicationContext)
    }

    @Test
    fun test_getAllAppsOnDevice_returnsAListOfApps() {
        val allAppsOnDevice = appsRepository.getAllAppsOnDevice()
        // confirm that the mutable list allAppsOnDevice holds items
        assertTrue(allAppsOnDevice.isNotEmpty())
    }

    @Test
    fun test_getAllAppsOnDevice_returnsOnlyAppsInstalledByTheUserInAndroidVersionGreaterThan8(){
        val allAppsOnDevice = appsRepository.getAllAppsOnDevice()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            allAppsOnDevice.forEach {
                assertNotEquals(it.flags, ApplicationInfo.FLAG_SYSTEM)
            }
        }
    }
}