package com.salesground.zipbolt.ui.screen.homescreen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import com.salesground.zipbolt.databinding.HomeScreenRecyclerViewBinding
import com.salesground.zipbolt.model.ApplicationModel
import com.salesground.zipbolt.model.MediaModel
import com.salesground.zipbolt.ui.screen.homescreen.homescreencomponents.HomeScreenAppBar
import com.salesground.zipbolt.ui.screen.homescreen.recyclerviewadapter.HomeScreenRecyclerViewAdapter
import com.salesground.zipbolt.ui.screen.homescreen.recyclerviewadapter.datamodel.DataCategory
import com.salesground.zipbolt.ui.screen.homescreen.recyclerviewadapter.datamodel.HomeScreenRecyclerviewDataModel
import com.salesground.zipbolt.viewmodel.DeviceApplicationViewModel
import com.salesground.zipbolt.viewmodel.HomeScreenViewModel

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun HomeScreen(
    deviceApplicationViewModel: DeviceApplicationViewModel,
    homeScreenViewModel: HomeScreenViewModel
) {
    val modalBottomSheetState =
        rememberModalBottomSheetState(
            initialValue = ModalBottomSheetValue.Hidden
        )

    val allApplicationsOnDevice by deviceApplicationViewModel.allApplicationsOnDevice.observeAsState(
        listOf<ApplicationModel>()
    )
    val deviceImages by homeScreenViewModel.deviceImages.observeAsState(mutableListOf())
    val deviceVideos by homeScreenViewModel.deviceVideos.observeAsState(mutableListOf())
    val deviceApplication by homeScreenViewModel.deviceApplications.observeAsState(mutableListOf())
    val deviceAudio by homeScreenViewModel.deviceAudio.observeAsState(mutableListOf())
    val homeScreenData by homeScreenViewModel.homeScreenData.observeAsState(mutableListOf())

    ModalBottomSheetLayout(
        sheetContent = {
            Text("")
        },
        sheetState = modalBottomSheetState
    ) {
        Scaffold(topBar = { HomeScreenAppBar() }) {
            Column(modifier = Modifier.padding(it)) {
                HomeScreenRecyclerViewComposeIntegration(
                    deviceApplication,
                    deviceImages,
                    deviceVideos,
                    deviceAudio
                )
            }
        }
    }
}

@Composable
private fun HomeScreenRecyclerViewComposeIntegration(
    deviceApplication: List<ApplicationModel>,
    deviceImages: List<MediaModel>,
    deviceVideos: List<MediaModel>,
    deviceAudio: List<MediaModel>
) {
    AndroidViewBinding(factory = HomeScreenRecyclerViewBinding::inflate) {
        val hSRAdapter = HomeScreenRecyclerViewAdapter()
        hSRAdapter.submitList(
            mutableListOf(
                HomeScreenRecyclerviewDataModel("Apps",
                    deviceApplication.map {
                        DataCategory.Application(it)
                    }),
                HomeScreenRecyclerviewDataModel("Images",
                    deviceImages.map {
                        DataCategory.Image(it)
                    }),
                HomeScreenRecyclerviewDataModel("Videos",
                    deviceVideos.map {
                        DataCategory.Video(it)
                    }),
                HomeScreenRecyclerviewDataModel("Music",
                    deviceAudio.map {
                        DataCategory.Music(it)
                    })
            )
        )
        homeScreenRecyclerView.adapter = hSRAdapter
    }
}