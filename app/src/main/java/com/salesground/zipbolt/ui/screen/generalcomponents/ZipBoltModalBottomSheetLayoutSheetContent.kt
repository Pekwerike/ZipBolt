package com.salesground.zipbolt.ui.screen.generalcomponents

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.salesground.zipbolt.R

val connectionOptions = listOf<ConnectionCategory>(
    ConnectionCategory.ANDROID,
    ConnectionCategory.IPHONE,
    ConnectionCategory.DESKTOP
)

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
fun ZipBoltModalBottomSheetContent() {
    Surface {
        Column(
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center) {
                Surface(
                    modifier = Modifier.requiredSize(width = 50.dp, height = 5.dp),
                    shape = MaterialTheme.shapes.large.copy(all = CornerSize(20.dp)),
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
                ) {

                }
            }
            Text(
                text = "Connect to", style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
            connectionOptions.forEach {
                DevicesConnectionCategoryDisplay(
                    platformLogoResourceId = it.categoryLogoResourceId,
                    platformLogoContentDescription = it.categoryLogoContentDescription,
                    actionLabel = it.actionLabel,
                    onConnectionCategoryClicked = {})
            }
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth(0.48f)
                            .padding(
                                end = 8.dp,
                                start = 4.dp
                            )
                    )
                    Text(
                        text = "Or", style = MaterialTheme.typography.caption,
                        modifier = Modifier.wrapContentWidth()
                    )
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth(1f)
                            .padding(
                                start = 8.dp,
                                end = 4.dp
                            )
                    )
                }
            }
            DevicesConnectionCategoryDisplay(
                platformLogoResourceId = ConnectionCategory.SHARE_ZIP_BOLT.categoryLogoResourceId,
                platformLogoContentDescription = ConnectionCategory.SHARE_ZIP_BOLT.categoryLogoContentDescription,
                actionLabel = ConnectionCategory.SHARE_ZIP_BOLT.actionLabel,
                onConnectionCategoryClicked = {})
        }
    }
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
