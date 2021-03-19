package com.salesground.zipbolt.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.salesground.zipbolt.fakerepository.FakeZipBoltImageRepository
import com.salesground.zipbolt.getOrAwaitValue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class DeviceMediaViewModelTest {
    private lateinit var deviceMediaViewModel: DeviceMediaViewModel

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        deviceMediaViewModel = DeviceMediaViewModel(imageRepository = FakeZipBoltImageRepository())
    }

    @Test
    fun testing() {
        val deviceImagesGroupedByDateAdded =
            deviceMediaViewModel.deviceImagesGroupedByDateModified.getOrAwaitValue(
                time = 1,
                timeUnit = TimeUnit.MINUTES
            )
        assert(!deviceImagesGroupedByDateAdded.isNullOrEmpty())
    }
}