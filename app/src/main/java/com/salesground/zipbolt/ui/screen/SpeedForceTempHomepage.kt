package com.salesground.zipbolt.ui.screen

import android.net.wifi.p2p.WifiP2pDevice
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.salesground.zipbolt.viewmodel.MainActivityViewModel

@Composable
fun HomeScreen(
    mainActivityViewModel: MainActivityViewModel,
    sendAction: () -> Unit, receiveAction: () -> Unit
) {

    val listOfDiscoveredDevices = mainActivityViewModel.discoveredPeersListState
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        LazyColumn(content = {
            items(listOfDiscoveredDevices.value) { device ->
                DiscoveredPeerUI(device = device)
            }
        })

        Row() {
            Button(onClick = sendAction) {

                Text(text = "Send")
            }
            Spacer(modifier = Modifier.padding(10.dp))
            Button(onClick = receiveAction) {
                Text(text = "Receive")
            }
        }
    }

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