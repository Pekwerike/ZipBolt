package com.salesground.zipbolt.ui.screen.homescreen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import com.salesground.zipbolt.databinding.HomeScreenRecyclerViewBinding
import com.salesground.zipbolt.model.ApplicationModel
import com.salesground.zipbolt.ui.screen.homescreen.homescreencomponents.DeviceApplication
import com.salesground.zipbolt.ui.screen.homescreen.recyclerviewadapter.HomeScreenRecyclerViewAdapter
import com.salesground.zipbolt.ui.screen.homescreen.recyclerviewadapter.datamodel.DataCategory
import com.salesground.zipbolt.ui.screen.homescreen.recyclerviewadapter.datamodel.HomeScreenRecyclerviewDataModel
import com.salesground.zipbolt.viewmodel.DeviceApplicationViewModel
import com.salesground.zipbolt.viewmodel.HomeScreenViewModel

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun HomeScreen(deviceApplicationViewModel: DeviceApplicationViewModel,
homeScreenViewModel: HomeScreenViewModel) {
    val modalBottomSheetState =
        rememberModalBottomSheetState(
            initialValue = ModalBottomSheetValue.Hidden
        )

    val allApplicationsOnDevice by deviceApplicationViewModel.allApplicationsOnDevice.observeAsState(
        listOf<ApplicationModel>()
    )
    val homeScreenData by homeScreenViewModel.homeScreenData.observeAsState(mutableListOf())
    ModalBottomSheetLayout(
        sheetContent = {
            Text("")
        },
        sheetState = modalBottomSheetState
    ) {
      /*  LazyVerticalGrid(cells = GridCells.Adaptive(128.dp)){
            items(count = allApplicationsOnDevice.size) { index: Int ->
                DeviceApplication(application = allApplicationsOnDevice[index])
            }
        }*/

        AndroidViewBinding(factory = HomeScreenRecyclerViewBinding::inflate){
            val hSRAdapter = HomeScreenRecyclerViewAdapter()
            hSRAdapter.submitList(homeScreenData)
            homeScreenRecyclerView.adapter = hSRAdapter
        }
    }
}