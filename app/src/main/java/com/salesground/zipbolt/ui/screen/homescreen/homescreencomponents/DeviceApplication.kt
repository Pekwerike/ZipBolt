package com.salesground.zipbolt.ui.screen.homescreen.homescreencomponents

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Size
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.core.graphics.drawable.toBitmap
import androidx.core.widget.ImageViewCompat
import coil.request.ImageRequest
import com.bumptech.glide.Glide
import com.salesground.zipbolt.databinding.AppIconImageViewBinding
import com.salesground.zipbolt.model.ApplicationModel
import dev.chrisbanes.accompanist.coil.CoilImage
import dev.chrisbanes.accompanist.imageloading.toPainter
import kotlinx.coroutines.Dispatchers
import java.io.File

@Composable
fun DeviceApplication(application: ApplicationModel) {
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //AppIconDisplay(application.appIcon)
        CoilImage(request = ImageRequest.Builder(context)
            .apply {
                size(100, 100)
                data(application.appIcon)
                dispatcher(Dispatchers.Default)
                allowHardware(true)
            }.build()
        ) {

        }

       /* application.appIcon?.let {
            Image(painter = application.appIcon.toPainter(), contentDescription = "")
        }*/
        Text(text = application.applicationName ?: "")
    }
}
