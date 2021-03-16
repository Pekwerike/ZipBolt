package com.salesground.zipbolt.ui.screen.navgraph

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.salesground.zipbolt.ui.navigation.ScreenRoutes
import com.salesground.zipbolt.ui.screen.homescreen.HomeScreen
import com.salesground.zipbolt.viewmodel.HomeScreenViewModel

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun ZipBoltNavGraph(
    navController: NavHostController,
    homeScreenViewModel: HomeScreenViewModel
) {
    NavHost(navController = navController, startDestination = ScreenRoutes.HOME_SCREEN.route) {
        composable(ScreenRoutes.HOME_SCREEN.route) {
            HomeScreen(homeScreenViewModel = homeScreenViewModel)
        }
        composable(ScreenRoutes.NOTIFICATION_SCREEN.route) {

        }
    }
}