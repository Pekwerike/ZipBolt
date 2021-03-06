package com.salesground.zipbolt.ui.temporaryscreens

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.salesground.zipbolt.model.MediaModel
import com.salesground.zipbolt.viewmodel.MediaViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@ExperimentalFoundationApi
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun HomeScreenTwo(mediaViewModel: MediaViewModel) {
    val allImagesOnDevice = mediaViewModel.allImagesOnDevice
    val allImagesFetchedOnce by mediaViewModel.allImagesFetchedOnce.observeAsState()
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState()
    val homeScreenCoroutineScope = rememberCoroutineScope()
    BottomSheetScaffold(
        sheetContent = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(15.dp), contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .height(10.dp)
                        .background(color = Color.DarkGray.copy(alpha = 0.5f))
                )
            }
            ImagesOnDeviceList(images = allImagesFetchedOnce?: mutableListOf())
        },
        scaffoldState = bottomSheetScaffoldState,
        sheetPeekHeight = 30.dp
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = {
                homeScreenCoroutineScope.launch {
                    bottomSheetScaffoldState.bottomSheetState.expand()
                }
            }) {
                Text(text = "Open")
            }
        }
    }
}

