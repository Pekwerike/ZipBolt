package com.salesground.zipbolt.ui.screen.homescreen.homescreencomponents

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.runtime.Composable
import com.salesground.zipbolt.ui.navigation.AppScreens
import com.salesground.zipbolt.ui.navigation.NavigationAction

val bottomNavigationScreens = listOf<AppScreens>(
    AppScreens.HomeScreen,
    AppScreens.NotificationScreen
)

@Composable
fun ZipBoltBottomNavigationItem(currentScreen: String?, navigationAction: NavigationAction) {
    BottomNavigation(backgroundColor = MaterialTheme.colors.surface) {
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
                })
        }
    }
}
