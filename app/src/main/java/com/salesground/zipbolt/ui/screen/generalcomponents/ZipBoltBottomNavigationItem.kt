package com.salesground.zipbolt.ui.screen.homescreen.homescreencomponents

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.salesground.zipbolt.ui.navigation.AppScreens
import com.salesground.zipbolt.ui.navigation.NavigationAction

val bottomNavigationScreens = listOf<AppScreens>(
    AppScreens.HomeScreen,
    AppScreens.NotificationScreen
)

@Composable
fun ZipBoltBottomNavigationItem(currentScreen: String?, navigationAction: NavigationAction) {
    BottomNavigation(backgroundColor = MaterialTheme.colors.surface, elevation = 2.dp) {
        bottomNavigationScreens.forEach {
            BottomNavigationItem(
                selected = it.route == currentScreen,
                onClick = when (it) {
                    AppScreens.HomeScreen -> navigationAction.navigateToHomeScreen
                    AppScreens.NotificationScreen -> navigationAction.navigateToNotificationScreen
                }, icon = {
                    when (it) {
                        AppScreens.HomeScreen -> Icon(
                            imageVector = Icons.Rounded.Home,
                            contentDescription = ""
                        )
                        AppScreens.NotificationScreen -> {
                            Icon(imageVector = Icons.Rounded.Notifications, contentDescription = "")
                        }
                    }
                },
                label = {
                    when (it) {
                        AppScreens.HomeScreen -> Text(text = "Home")
                        AppScreens.NotificationScreen -> Text(text = "Notification")
                    }
                },
                selectedContentColor = MaterialTheme.colors.primary
            )
        }
    }
}
