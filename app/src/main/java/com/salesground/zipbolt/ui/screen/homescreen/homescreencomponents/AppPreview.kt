package com.salesground.zipbolt.ui.screen.homescreen.homescreencomponents

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import coil.request.ImageRequest
import com.salesground.zipbolt.model.ApplicationModel
import dev.chrisbanes.accompanist.coil.CoilImage

@Composable
fun AppReview(application: ApplicationModel){
    val context = LocalContext.current
    Column {
        CoilImage(request = ImageRequest.Builder(context).apply {
            data(application.appIcon)
        }.build()) {

        }
        Text(text = application.applicationName ?: "NaN")
    }
}