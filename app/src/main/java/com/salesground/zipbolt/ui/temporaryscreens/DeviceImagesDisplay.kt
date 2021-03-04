package com.salesground.zipbolt.ui.temporaryscreens

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.request.ImageRequest
import com.salesground.zipbolt.model.MediaModel
import dev.chrisbanes.accompanist.coil.CoilImage


@ExperimentalFoundationApi
@Composable
fun ImagesOnDeviceList(images: MutableList<MediaModel>) {
    LazyVerticalGrid(cells = GridCells.Adaptive(minSize = 100.dp)) {
        items(images.size){ imageIndex ->
            SingleImageOnDevice(image = images.get(imageIndex))
        }
    }
}

@Composable
fun SingleImageOnDevice(image: MediaModel) {
    CoilImage(
        data = image.mediaUri, contentScale = ContentScale.Crop, fadeIn = true,
        contentDescription = ""
    )

}
