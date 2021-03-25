package com.salesground.zipbolt.ui.screen.allmediadisplay.categorycontentsdisplay.images

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salesground.zipbolt.ui.screen.allmediadisplay.categorycontentsdisplay.images.recyclerview.BucketChipGroup
import com.salesground.zipbolt.viewmodel.ImagesViewModel

@Composable
fun ImagesBucketsDisplayComposable(imagesViewModel: ImagesViewModel) {
    val deviceImagesBuckets by imagesViewModel.deviceImagesBucketName.observeAsState()
    val chosenBucketName by imagesViewModel.chosenBucket.observeAsState()

    Column {
        BucketChipGroup(
            modifier = Modifier
                .padding(5.dp)
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