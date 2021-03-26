package com.salesground.zipbolt.ui.screen.navgraph

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.*
import com.salesground.zipbolt.R
import com.salesground.zipbolt.ui.navigation.NavigationAction
import com.salesground.zipbolt.ui.screen.generalcomponents.ZipBoltMainFloatingActionButton
import com.salesground.zipbolt.ui.screen.homescreen.homescreencomponents.ZipBoltBottomNavigationItem
import com.salesground.zipbolt.viewmodel.HomeScreenViewModel


@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun ZipBoltUIEntryPoint(homeScreenViewModel: HomeScreenViewModel) {


    val modalBottomSheetState =
        rememberModalBottomSheetState(
            initialValue = ModalBottomSheetValue.Hidden
        )
    val navController = rememberNavController()
        val currentScreen =
            navController.currentBackStackEntryAsState().value?.arguments?.getString(
                KEY_ROUTE
            )
        val navigationActions = remember(navController) { NavigationAction(navController) }

        ModalBottomSheetLayout(sheetContent = {
            Text(text = "")
        }, sheetState = modalBottomSheetState) {
            Scaffold(bottomBar = {
                ZipBoltBottomNavigationItem(
                    currentScreen = currentScreen, navigationAction =
                    navigationActions
                )
            }, floatingActionButtonPosition = FabPosition.End,
            floatingActionButton = {
                ZipBoltMainFloatingActionButton(onClick = { /*TODO*/ })
            }) {
                ZipBoltNavGraph(navController = navController,
                homeScreenViewModel = homeScreenViewModel)
            }
        }
}