package com.salesground.zipbolt.ui.screen.generalcomponents

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

enum class ConnectionCategory(
    val actionLabel: String,
    val categoryLogo: ImageVector = Icons.Rounded.Settings,
    val categoryLogoContentDescription: String = ""
) {
    ANDROID(actionLabel = "Connect to Android device"),
    IPHONE(actionLabel = "Connect to Iphone"),
    DESKTOP(actionLabel = "Connect to Desktop"),
    SHARE_ZIP_BOLT(actionLabel = "Share ZipBolt")
}

@Composable
fun DevicesConnectionCategoryDisplay(
    platformLogo: ImageVector, platformLogoContentDescription: String,
    actionLabel: String, onConnectionCategoryClicked: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(4.dp)
            .clickable {
                onConnectionCategoryClicked(actionLabel)
            }
            .padding(4.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = platformLogo, contentDescription = platformLogoContentDescription,
            modifier = Modifier.padding(5.dp)
        )
        Text(
            text = actionLabel, modifier = Modifier.padding(5.dp),
            style = MaterialTheme.typography.body1
        )
    }
}