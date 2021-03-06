package com.salesground.zipbolt.ui.temporaryscreens

import android.content.Context
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.setValue
import com.salesground.zipbolt.viewmodel.MainActivityViewModel
import com.salesground.zipbolt.viewmodel.MediaViewModel

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
fun TempHomeScreen(mediaViewModel: MediaViewModel, context : Context) {
    val allImagesOnDevice = mediaViewModel.allImagesOnDevice
    val allImagesFetchedOnce by mediaViewModel.allImagesFetchedOnce.observeAsState()
    //ImagesOnDeviceList(images = allImagesFetchedOnce?: mutableListOf(), context)
}