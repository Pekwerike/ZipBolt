package com.salesground.zipbolt.ui.screen.homescreen.homescreencomponents

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreenAppBar() {
    TopAppBar(
        title = {
            Text(
                text = "ZipBolt", textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }, backgroundColor = MaterialTheme.colors.surface,
        modifier = Modifier.fillMaxWidth(), elevation = 2.dp
    )
}
