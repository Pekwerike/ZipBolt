package com.salesground.zipbolt.repository

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Before

import org.junit.Assert.*

class AppsRepositoryTest {

    private val applicationContext = ApplicationProvider.getApplicationContext<Context>()
    private lateinit var appsRepository: AppsRepository

    @Before
    fun setUp() {
        appsRepository = AppsRepository(applicationContext)
    }
}