package com.salesground.zipbolt.ui.navigation

import androidx.navigation.NavHostController
import androidx.navigation.compose.navigate


enum class ScreenRoutes(val route: String){
    HOME_SCREEN("home"),
    NOTIFICATION_SCREEN("notification")
}

sealed class AppScreens(val route : String) {
    object HomeScreen : AppScreens(ScreenRoutes.HOME_SCREEN.route)
    object NotificationScreen : AppScreens(ScreenRoutes.NOTIFICATION_SCREEN.route)
}

class NavigationAction(private val navController: NavHostController){

    val navigateToNotificationScreen = {
        navController.navigate(ScreenRoutes.NOTIFICATION_SCREEN.route){
            popUpTo = navController.graph.startDestination
            launchSingleTop = true
        }
    }

    val navigateToHomeScreen = {
        navController.navigate(ScreenRoutes.HOME_SCREEN.route){
            popUpTo = navController.graph.startDestination
            launchSingleTop = true
        }
    }
}