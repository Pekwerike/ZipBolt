package com.salesground.zipbolt.repository

import android.content.Context
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
}