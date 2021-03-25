package com.salesground.zipbolt.ui.screen.generalcomponents

import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun ZipBoltMainFloatingActionButton(
    icon: ImageVector = Icons.Rounded.Person,
    label: String = "Connect",
    onClick: () -> Unit) {

    ExtendedFloatingActionButton(text = {
        Text(text = label)
    }, onClick = onClick,
        icon = { Icon(imageVector = icon, contentDescription = "") },
        backgroundColor = MaterialTheme.colors.primary,
        shape = MaterialTheme.shapes.small.copy(all = CornerSize(10.dp))
    )
}