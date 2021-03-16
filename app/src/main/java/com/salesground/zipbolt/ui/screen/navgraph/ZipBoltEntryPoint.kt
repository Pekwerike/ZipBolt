package com.salesground.zipbolt.ui.screen.navgraph

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.salesground.zipbolt.ui.navigation.AppScreens
import com.salesground.zipbolt.ui.navigation.NavigationAction
import com.salesground.zipbolt.ui.navigation.ScreenRoutes
import com.salesground.zipbolt.ui.screen.homescreen.homescreencomponents.HomeScreenBottomNavigationItem


@Composable
fun ZipBoltNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = ScreenRoutes.HOME_SCREEN.route) {
        composable(ScreenRoutes.HOME_SCREEN.route) {

        }
        composable(ScreenRoutes.NOTIFICATION_SCREEN.route) {

        }
    }
}

@ExperimentalMaterialApi
@Composable
fun ZipBoltEntryPoint() {
    val navController = rememberNavController()
    val currentScreen = navController.currentBackStackEntryAsState().value?.arguments?.getString(
        KEY_ROUTE)
    val navigationActions = remember(navController){ NavigationAction(navController) }
        ModalBottomSheetLayout(sheetContent = {
            Text(text = "")
        }) {
            Scaffold(bottomBar = {
                HomeScreenBottomNavigationItem(currentScreen = currentScreen, navigationAction =
                navigationActions)
            }) {
                ZipBoltNavGraph(navController = navController)
            }
        }
}