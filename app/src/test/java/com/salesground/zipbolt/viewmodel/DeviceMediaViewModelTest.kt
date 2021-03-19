package com.salesground.zipbolt.viewmodel

import com.salesground.zipbolt.fakedatasource.FakeZipBoltImageRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class DeviceMediaViewModelTest {
    private lateinit var deviceMediaViewModel : DeviceMediaViewModel

    @Before
    fun setUp(){
        deviceMediaViewModel = DeviceMediaViewModel(imageRepository = FakeZipBoltImageRepository())
    }

    @Test
    fun testing(){
        
    }
}