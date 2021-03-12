package com.salesground.zipbolt.ui.screen.homescreen.homescreencomponents

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable

@Composable
fun HomeScreenAppBar() {
    TopAppBar(title = {
        Text(text = "ZipBolt by Salesground")
    }, backgroundColor = MaterialTheme.colors.surface)
}