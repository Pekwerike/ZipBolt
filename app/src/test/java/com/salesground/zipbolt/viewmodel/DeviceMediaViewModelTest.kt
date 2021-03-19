package com.salesground.zipbolt.viewmodel

import com.salesground.zipbolt.fakerepository.FakeZipBoltImageRepository
import org.junit.Before
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