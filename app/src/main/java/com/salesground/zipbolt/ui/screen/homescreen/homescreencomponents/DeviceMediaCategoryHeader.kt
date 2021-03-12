package com.salesground.zipbolt.ui.screen.homescreen.homescreencomponents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
fun DeviceMediaCategoryHeader(categoryName : String){
    Row(horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = categoryName)
        OutlinedButton(onClick = { }) {
            Text(text = "view all")
        }
    }
}