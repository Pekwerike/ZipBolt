package com.salesground.zipbolt.ui.screen.homescreen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.salesground.zipbolt.databinding.HomeScreenRecyclerViewBinding
import com.salesground.zipbolt.model.ApplicationModel
import com.salesground.zipbolt.model.MediaModel
import com.salesground.zipbolt.ui.navigation.NavigationAction
import com.salesground.zipbolt.ui.screen.homescreen.homescreencomponents.HomeScreenAppBar
import com.salesground.zipbolt.ui.screen.homescreen.homescreencomponents.HomeScreenRecyclerViewComposeConfiguration
import com.salesground.zipbolt.ui.screen.homescreen.recyclerviewadapter.HomeScreenRecyclerViewAdapter
import com.salesground.zipbolt.ui.screen.homescreen.recyclerviewadapter.datamodel.DataCategory
import com.salesground.zipbolt.ui.screen.homescreen.recyclerviewadapter.datamodel.HomeScreenRecyclerviewDataModel
import com.salesground.zipbolt.viewmodel.DeviceApplicationViewModel
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
