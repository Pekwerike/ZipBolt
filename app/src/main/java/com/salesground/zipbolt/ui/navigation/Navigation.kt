package com.salesground.zipbolt.ui.navigation

import androidx.navigation.NavHostController
import androidx.navigation.compose.navigate


enum class ScreenRoutes(val route: String){
    HOME_SCREEN("home"),
    NOTIFICATION_SCREEN("notification")
}

sealed class AppScreens(val route : String) {
    object HomeScreen : AppScreens("Home")
    object NotificationScreen : AppScreens("Notification")
}

class NavigationAction(private val navController: NavHostController){

    val navigateToNotificationScreen = {
        navController.navigate(ScreenRoutes.NOTIFICATION_SCREEN.route){
            popUpTo = navController.graph.startDestination
            launchSingleTop
        }
    }

    val navigateToHomeScreen = {
        navController.navigate(ScreenRoutes.HOME_SCREEN.route){
            popUpTo = navController.graph.startDestination
            launchSingleTop
        }
    }
}