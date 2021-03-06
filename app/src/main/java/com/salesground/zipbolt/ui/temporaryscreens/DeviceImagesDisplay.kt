package com.salesground.zipbolt.ui.temporaryscreens

import android.content.Context
import android.os.Build
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.primarySurface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.request.ImageRequest
import com.salesground.zipbolt.model.MediaModel
import com.salesground.zipbolt.ui.theme.typography
import dev.chrisbanes.accompanist.coil.CoilImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
fun ImagesOnDeviceList(images: MutableList<MediaModel>) {

    val groupedBy = images.groupBy {
        it.mediaBucketName
    }
    LazyColumn(modifier = Modifier.fillMaxSize(1f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center, content = {
        groupedBy.forEach { (s, list) ->
            stickyHeader {
                Text(
                    s,
                    modifier = Modifier
                        .background(MaterialTheme.colors.surface)
                        .padding(5.dp)
                        .fillMaxWidth(1f),
                    style = MaterialTheme.typography.h6
                )
            }
            val rowCount = list.size / 4 + if (list.size % 4 == 0) 0 else 1
            items(rowCount) { rowIndex ->
                Row(
                    modifier = Modifier.fillMaxWidth(1f),
                    horizontalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    for (i in 4 * rowIndex until 4 * (1 + rowIndex)) {
                        if (i < list.size) {
                            SingleImageOnDevice(list[i])
                        } else break
                    }
                }
            }
        }
    })
}


@ExperimentalAnimationApi
@Composable
fun SingleImageOnDevice(image: MediaModel) {
    var imageClicked by remember { mutableStateOf<Boolean>(false) }
    val animatedImageSize by animateDpAsState(targetValue = if (imageClicked) 70.dp else 100.dp)
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
                data = image.mediaUri, contentScale = ContentScale.Crop, fadeIn = true,
                contentDescription = ""
            )
            AnimatedVisibility(visible = imageClicked) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(1f)
                        .background(Color.Green.copy(alpha = 0.2f))
                )
            }
        }
}

