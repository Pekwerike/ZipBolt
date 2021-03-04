package com.salesground.zipbolt.ui.temporaryscreens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.salesground.zipbolt.viewmodel.MainActivityViewModel
import com.salesground.zipbolt.viewmodel.MediaViewModel

@ExperimentalFoundationApi
@Composable
fun TempHomeScreen(mediaViewModel: MediaViewModel) {
    val allImagesOnDevice = mediaViewModel.allImagesOnDevice
    ImagesOnDeviceList(images = allImagesOnDevice.value)
}