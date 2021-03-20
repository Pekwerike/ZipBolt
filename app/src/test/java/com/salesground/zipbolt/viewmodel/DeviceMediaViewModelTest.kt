package com.salesground.zipbolt.viewmodel

import android.os.Looper.getMainLooper
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.salesground.zipbolt.fakerepository.FakeZipBoltImageRepository
import com.salesground.zipbolt.getOrAwaitValue
import com.salesground.zipbolt.ui.screen.categorycontentsdisplay.images.dto.ImagesDisplayModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(manifest=Config.NONE)
class DeviceMediaViewModelTest {
    private lateinit var deviceMediaViewModel: DeviceMediaViewModel

    @get:Rule
    var instantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        deviceMediaViewModel = DeviceMediaViewModel(imageRepository = FakeZipBoltImageRepository())
    }

    @ExperimentalCoroutinesApi
    @Test
    fun testing() {
        shadowOf(getMainLooper()).idle()
        val imagesOnDevice =
            deviceMediaViewModel.deviceImagesGroupedByDateModified.getOrAwaitValue()
        assert(!imagesOnDevice.isNullOrEmpty())
        imagesOnDevice.forEach {
            when(it){
                is ImagesDisplayModel.DeviceImageDisplay -> println(it.deviceImage.imageDisplayName)
                is ImagesDisplayModel.ImagesDateModifiedHeader -> println(it.dateModified)
            }
        }
    }
}