package com.salesground.zipbolt.ui.screen.homescreen

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import com.salesground.zipbolt.ui.screen.homescreen.homescreencomponents.DeviceMediaCategoryHeader
import com.salesground.zipbolt.viewmodel.DeviceApplicationViewModel

@ExperimentalMaterialApi
@Composable
fun HomeScreen(deviceApplicationViewModel: DeviceApplicationViewModel) {
    val modalBottomSheetState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val allApplicationsOnDevice by deviceApplicationViewModel.allApplicationsOnDevice.observeAsState()
    ModalBottomSheetLayout(
        sheetContent = { },
        sheetState = modalBottomSheetState
    ) {
        LazyColumn(content = {
            item {
                DeviceMediaCategoryHeader(categoryName = "App")
            }

        })
    }
}