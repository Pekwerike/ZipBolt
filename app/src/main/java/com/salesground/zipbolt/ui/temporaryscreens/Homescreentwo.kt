package com.salesground.zipbolt.ui.temporaryscreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@ExperimentalMaterialApi
@Composable
fun HomeScreenTwo(){
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState()
    BottomSheetScaffold(sheetContent = {
        Box(modifier = Modifier.fillMaxWidth().height(20.dp), contentAlignment = Alignment.Center ){
            Box(modifier = Modifier.width(30.dp).height(15.dp).background(color =  Color.DarkGray.copy(alpha = 0.5f)))
        }
    }) {

    }
}

