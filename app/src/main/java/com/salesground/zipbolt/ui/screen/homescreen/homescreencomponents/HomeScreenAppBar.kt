package com.salesground.zipbolt.ui.screen.homescreen.homescreencomponents

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreenAppBar() {
    TopAppBar(title = {
        Text(text = "ZipBolt", textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth())
    }, backgroundColor = MaterialTheme.colors.surface,
    modifier = Modifier.fillMaxWidth(), elevation = 0.dp)
}