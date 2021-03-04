package com.salesground.zipbolt.ui.temporaryscreens

import android.content.Context
import android.widget.ImageView
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.request.ImageRequest
import com.salesground.zipbolt.model.MediaModel
import dev.chrisbanes.accompanist.coil.CoilImage


@ExperimentalFoundationApi
@Composable
fun ImagesOnDeviceList(images: MutableList<MediaModel>) {

    val groupBy = images.groupBy {
        it.mediaBucketName
    }
    LazyVerticalGrid(cells = GridCells.Adaptive(minSize = 100.dp)) {
        items(images.size) { imageIndex ->
            SingleImageOnDevice(image = images[imageIndex])
        }
    }


}

@Composable
fun SingleImageOnDevice(image: MediaModel) {
    CoilImage(
        modifier = Modifier.padding(1.dp).height(100.dp).clickable {

        },
        data = image.mediaUri, contentScale = ContentScale.Crop, fadeIn = true,
        contentDescription = ""
    )
}
