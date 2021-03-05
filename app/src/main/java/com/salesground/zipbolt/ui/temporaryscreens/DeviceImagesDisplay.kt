package com.salesground.zipbolt.ui.temporaryscreens

import android.content.Context
import android.widget.ImageView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.request.ImageRequest
import com.salesground.zipbolt.model.MediaModel
import dev.chrisbanes.accompanist.coil.CoilImage


@ExperimentalAnimationApi
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

@ExperimentalAnimationApi
@Composable
fun SingleImageOnDevice(image: MediaModel) {
    var imageClicked by remember { mutableStateOf<Boolean>(false) }
    val animatedImageSize by animateDpAsState(targetValue = if(imageClicked) 70.dp else 100.dp)
    Box(
        modifier = Modifier
            .size(100.dp),
        contentAlignment = Alignment.Center
    ) {
        CoilImage(
            modifier = Modifier
                .padding(1.dp)
                .size(animatedImageSize)
                .clickable {
                    imageClicked = !imageClicked
                },
            colorFilter = ColorFilter.tint(Color.Yellow),
            data = image.mediaUri, contentScale = ContentScale.Crop, fadeIn = true,
            contentDescription = ""
        )
        AnimatedVisibility(visible = imageClicked) {
            Icon(imageVector = Icons.Rounded.CheckCircle, contentDescription = "")
        }
    }
}

