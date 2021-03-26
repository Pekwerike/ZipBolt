package com.salesground.zipbolt.ui.screen.generalcomponents

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.salesground.zipbolt.R
import dev.chrisbanes.accompanist.coil.CoilImage

enum class ConnectionCategory(
    val actionLabel: String,
    val categoryLogoResourceId: Int,
    val categoryLogoContentDescription: String
) {
    ANDROID(
        actionLabel = "Android",
        categoryLogoResourceId = R.drawable.android_icon,
        categoryLogoContentDescription = "Connect to Android"
    ),
    IPHONE(
        actionLabel = "iPhone",
        categoryLogoResourceId = R.drawable.multi_colored_apple_icon,
        categoryLogoContentDescription = "Connect to iPhone"
    ),
    DESKTOP(
        actionLabel = "Desktop",
        categoryLogoResourceId = R.drawable.pc_icon,
        categoryLogoContentDescription = "Connect to PC"
    ),
    SHARE_ZIP_BOLT(
        actionLabel = "Share ZipBolt",
        categoryLogoResourceId = R.drawable.share_icon,
        categoryLogoContentDescription = "Share ZipBolt"
    )
}

@Composable
fun DevicesConnectionCategoryDisplay(
    platformLogoResourceId: Int, platformLogoContentDescription: String,
    actionLabel: String, onConnectionCategoryClicked: (String) -> Unit
) {

    Row(
        modifier = Modifier
            .clickable {
                onConnectionCategoryClicked(actionLabel)
            }
            .padding(8.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(platformLogoResourceId),
            contentDescription = platformLogoContentDescription,
            modifier = Modifier
                .padding(5.dp)
                .requiredSize(24.dp)
        )

        Text(
            text = actionLabel, modifier = Modifier.padding(5.dp),
            style = MaterialTheme.typography.body1
        )
    }
}