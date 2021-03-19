package com.salesground.zipbolt.viewmodel

import com.salesground.zipbolt.fakerepository.FakeZipBoltImageRepository
import com.salesground.zipbolt.getOrAwaitValue
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit


class DeviceMediaViewModelTest {
    private lateinit var deviceMediaViewModel : DeviceMediaViewModel

    @Before
    fun setUp(){
        deviceMediaViewModel = DeviceMediaViewModel(imageRepository = FakeZipBoltImageRepository())
    }

    @Test
    fun testing(){
        val deviceImagesGroupedByDateAdded = deviceMediaViewModel.deviceImagesGroupedByDateModified.getOrAwaitValue(
            time = 1,
            timeUnit = TimeUnit.MINUTES
        )

    }
}