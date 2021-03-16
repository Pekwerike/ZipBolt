package com.salesground.zipbolt.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigate


sealed class AppScreen(val route : String) {
    object HomeScreen : AppScreen("Home")
    object NotificationScreen : AppScreen("Notification")
}

class NavigationAction(private val navController: NavController){

    val navigateToNotificationScreen = {
        navController.navigate(AppScreen.NotificationScreen.route){
            popUpTo = navController.graph.startDestination
            launchSingleTop
        }
    }

    val navigateToHomeScreen = {
        navController.navigate(AppScreen.HomeScreen.route){
            popUpTo = navController.graph.startDestination
            launchSingleTop
        }
    }
}