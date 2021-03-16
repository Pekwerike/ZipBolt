package com.salesground.zipbolt.ui.screen.navgraph

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.salesground.zipbolt.ui.navigation.AppScreens
import com.salesground.zipbolt.ui.navigation.ScreenRoutes

val bottomNavigationScreens = listOf<AppScreens>(
    AppScreens.HomeScreen,
    AppScreens.NotificationScreen
)

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
    val currentScreen =
        ModalBottomSheetLayout(sheetContent = {
            Text(text = "")
        }) {
            Scaffold(bottomBar = {
                BottomNavigation(backgroundColor = MaterialTheme.colors.surface) {
                    bottomNavigationScreens.forEach {
                        BottomNavigationItem(
                            selected = it == currentScreen,
                            onClick = { /*TODO*/ }) {

                        }
                    }
                }
            }) {
                ZipBoltNavGraph(navController = navController)
            }
        }
}