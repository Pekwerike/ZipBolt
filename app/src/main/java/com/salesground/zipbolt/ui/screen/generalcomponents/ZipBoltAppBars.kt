package com.salesground.zipbolt.ui.screen.generalcomponents

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign

@Composable
fun ZipBoltAppBarWithHistory(
    appBarTitle: String, navigationIcon: ImageVector? = null,
    navigationIconContentDescription: String = "",
    navigationIconClicked: () -> Unit = {},
) {
    TopAppBar(
        title = {
            Text(
                text = appBarTitle, textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        navigationIcon = {
            if (navigationIcon != null) {
                IconButton(onClick = { navigationIconClicked() }) {
                    Icon(imageVector = navigationIcon, contentDescription =
                    navigationIconContentDescription)
                }
            }
        },
        actions = {
            IconButton(onClick = { /*TODO
            navigate to history screen*/
            }) {
                //Todo place history icon
            }
        }
    )
}