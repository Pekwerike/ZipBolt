package com.salesground.zipbolt.ui.screen.homescreen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidViewBinding
import com.salesground.zipbolt.databinding.HomeScreenRecyclerViewBinding
import com.salesground.zipbolt.model.ApplicationModel
import com.salesground.zipbolt.ui.screen.homescreen.homescreencomponents.DeviceApplication
import com.salesground.zipbolt.ui.screen.homescreen.homescreencomponents.DeviceMediaCategoryHeader
import com.salesground.zipbolt.ui.screen.homescreen.recyclerviewadapter.DataCategory
import com.salesground.zipbolt.ui.screen.homescreen.recyclerviewadapter.HomeScreenRecyclerViewAdapter
import com.salesground.zipbolt.ui.screen.homescreen.recyclerviewadapter.HomeScreenRecyclerviewDataModel
import com.salesground.zipbolt.viewmodel.DeviceApplicationViewModel

@ExperimentalMaterialApi
@Composable
fun HomeScreen(deviceApplicationViewModel: DeviceApplicationViewModel) {
    val modalBottomSheetState =
        rememberModalBottomSheetState(
            initialValue = ModalBottomSheetValue.Hidden
        )

    val allApplicationsOnDevice by deviceApplicationViewModel.allApplicationsOnDevice.observeAsState(
        listOf<ApplicationModel>()
    )
    ModalBottomSheetLayout(
        sheetContent = {
            Text("")
        },
        sheetState = modalBottomSheetState
    ) {
        LazyColumn(content = {
            item {
                DeviceMediaCategoryHeader(categoryName = "App")
            }
            items(count = allApplicationsOnDevice.size, key = {
                allApplicationsOnDevice[it].apkPath
            }) { index: Int ->
                DeviceApplication(application = allApplicationsOnDevice[index])
            }

        }, modifier = Modifier.fillMaxSize())

        AndroidViewBinding(factory = HomeScreenRecyclerViewBinding::inflate){
            val hSRAdapter = HomeScreenRecyclerViewAdapter()
            hSRAdapter.submitList(mutableListOf(HomeScreenRecyclerviewDataModel(
                dataCategory = "Apps",
                mediaCollection = allApplicationsOnDevice.map {
                    DataCategory.Application(it)
                } as MutableList<DataCategory>
            )))
            homeScreenRecyclerView.adapter
        }
    }
}