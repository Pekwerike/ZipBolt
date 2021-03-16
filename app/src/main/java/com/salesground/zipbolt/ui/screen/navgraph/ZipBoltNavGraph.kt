package com.salesground.zipbolt.ui.screen.navgraph

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.salesground.zipbolt.ui.navigation.ScreenRoutes

@Composable
fun ZipBoltNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = ScreenRoutes.HOME_SCREEN.route) {
        composable(ScreenRoutes.HOME_SCREEN.route) {

        }
        composable(ScreenRoutes.NOTIFICATION_SCREEN.route) {

        }
    }
}