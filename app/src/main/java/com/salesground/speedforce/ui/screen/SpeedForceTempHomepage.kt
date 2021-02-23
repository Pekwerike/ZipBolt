package com.salesground.speedforce.ui.screen

import android.net.wifi.p2p.WifiP2pDevice
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.salesground.speedforce.viewmodel.MainActivityViewModel

@Composable
fun HomeScreen(mainActivityViewModel: MainActivityViewModel) {
    val listOfDiscoveredDevices = mainActivityViewModel.discoveredPeersListState
    LazyColumn(content = {
        items(listOfDiscoveredDevices.value) { device ->
            DiscoveredPeerUI(device = device)
        }
    })
}

@Composable
fun DiscoveredPeerUI(device: WifiP2pDevice) {
    Row() {
        val deviceDetails = buildAnnotatedString {
            append(device.deviceName)
            withStyle(style = ParagraphStyle()) {
                append(device.deviceAddress)
            }
        }
        Text(text = deviceDetails)
    }
}