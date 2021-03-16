package com.salesground.zipbolt.ui.screen.generalcomponents

import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.runtime.Composable

@Composable
fun DiscoverPeerButton(openPeersDiscoverySheet: () -> Unit) {
    ExtendedFloatingActionButton(text = {
        Text(text = "Discover")
    }, onClick = openPeersDiscoverySheet,
        icon = { Icon(imageVector = Icons.Rounded.Person, contentDescription = "") },
        backgroundColor = MaterialTheme.colors.primary
    )
}