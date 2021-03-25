package com.salesground.zipbolt.ui.screen

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@ExperimentalMaterialApi
@Composable
fun UIEntryPoint() {

    ModalBottomSheetLayout(sheetContent = {

    }) {

    }
}

@Composable
fun ModalBottomSheetConnectionAction(
    platformLogo: ImageVector, platformLogoContentDescription: String,
    actionLabel: String
) {
    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = platformLogo, contentDescription = platformLogoContentDescription,
            modifier = Modifier.padding(5.dp)
        )
        Text(
            text = actionLabel, modifier = Modifier.padding(5.dp),
            style = MaterialTheme.typography.body2
        )
    }
}