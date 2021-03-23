package com.salesground.zipbolt.ui.screen.categorycontentsdisplay.images

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.recyclerview.widget.GridLayoutManager
//import com.salesground.zipbolt.databinding.MainRecyclerViewBinding
import com.salesground.zipbolt.ui.screen.categorycontentsdisplay.images.recyclerview.BucketChipGroup
import com.salesground.zipbolt.ui.screen.categorycontentsdisplay.images.recyclerview.DeviceImagesDisplayRecyclerViewAdapter
import com.salesground.zipbolt.ui.screen.categorycontentsdisplay.images.recyclerview.DeviceImagesDisplayViewHolderType
import com.salesground.zipbolt.viewmodel.ImagesViewModel

@Composable
fun ImagesBucketsDisplayComposable(imagesViewModel: ImagesViewModel) {
    val deviceImagesBuckets by imagesViewModel.deviceImagesBucketName.observeAsState()
    val chosenBucketName by imagesViewModel.chosenBucket.observeAsState()

    Column(modifier = Modifier
        .wrapContentHeight()) {
        BucketChipGroup(
            modifier = Modifier
                .padding(10.dp)
                .wrapContentHeight()
        ) {
            deviceImagesBuckets?.forEach {
                BucketChip(
                    bucketName = it.bucketName,
                    isChosen = it.bucketName == chosenBucketName
                ) {
                    imagesViewModel.filterDeviceImages(bucketName = it)
                }
            }
        }
    }
}