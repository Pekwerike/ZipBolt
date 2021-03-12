package com.salesground.zipbolt.ui.screen.homescreen

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import com.salesground.zipbolt.ui.screen.homescreen.homescreencomponents.DeviceMediaCategoryHeader

@ExperimentalMaterialApi
@Composable
fun HomeScreen() {
    val modalBottomSheetState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
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