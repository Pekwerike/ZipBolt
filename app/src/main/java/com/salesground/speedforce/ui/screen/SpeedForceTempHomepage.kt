package com.salesground.speedforce.ui.screen

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import com.salesground.speedforce.viewmodel.MainActivityViewModel

@Composable
fun HomeScreen(mainActivityViewModel : MainActivityViewModel){
    val listOfDiscoveredDevices = mainActivityViewModel.discoveredPeersList.observeAsState
    LazyColumn(content = { /*TODO*/ })
}