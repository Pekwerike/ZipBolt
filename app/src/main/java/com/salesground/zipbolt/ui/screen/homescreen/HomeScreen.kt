package com.salesground.zipbolt.ui.screen.homescreen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salesground.zipbolt.ui.screen.homescreen.homescreencomponents.HomeScreenAppBar
import com.salesground.zipbolt.ui.screen.homescreen.homescreencomponents.HomeScreenRecyclerViewComposeConfiguration
import com.salesground.zipbolt.viewmodel.HomeScreenViewModel

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun HomeScreen(
    homeScreenViewModel: HomeScreenViewModel
) {
    val deviceImages by homeScreenViewModel.deviceImages.observeAsState(mutableListOf())
    val deviceVideos by homeScreenViewModel.deviceVideos.observeAsState(mutableListOf())
    val deviceApplication by homeScreenViewModel.deviceApplications.observeAsState(mutableListOf())
    val deviceAudio by homeScreenViewModel.deviceAudio.observeAsState(mutableListOf())
    val homeScreenData by homeScreenViewModel.homeScreenData.observeAsState(mutableListOf())

    Column(modifier = Modifier.fillMaxSize()) {
        HomeScreenAppBar()
        HomeScreenRecyclerViewComposeConfiguration(
            deviceApplication,
            deviceImages,
            deviceVideos,
            deviceAudio
        )
    }

}
