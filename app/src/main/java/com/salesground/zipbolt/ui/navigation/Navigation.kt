package com.salesground.zipbolt.ui.navigation

import androidx.navigation.NavHostController

sealed class AppScreen() {
    object HomeScreen : AppScreen()
    object NotificationScreen : AppScreen()
}

class NavigationAction(private val navHostController: NavHostController){
    val navigateToNotificationScreen = {
        navHostController.navigate()
    }
}