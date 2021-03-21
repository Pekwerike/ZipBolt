package com.salesground.zipbolt.ui.screen.categorycontentsdisplay.images

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.recyclerview.widget.GridLayoutManager
import com.salesground.zipbolt.databinding.MainRecyclerViewBinding
import com.salesground.zipbolt.ui.screen.categorycontentsdisplay.images.recyclerview.DeviceImagesDisplayRecyclerViewAdapter
import com.salesground.zipbolt.ui.screen.categorycontentsdisplay.images.recyclerview.DeviceImagesDisplayViewHolderType
import com.salesground.zipbolt.viewmodel.ImagesViewModel

@Composable
fun DeviceImagesDisplayComposable(imagesViewModel: ImagesViewModel) {
    val groupedDeviceImages by imagesViewModel.deviceImagesGroupedByDateModified.observeAsState()
    val context = LocalContext.current

    AndroidViewBinding(factory = MainRecyclerViewBinding::inflate) {
        val adapter = DeviceImagesDisplayRecyclerViewAdapter()
        adapter.submitList(groupedDeviceImages)
        val layoutManager = GridLayoutManager(context, 4)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (adapter.getItemViewType(position)) {
                    DeviceImagesDisplayViewHolderType.IMAGE.type -> 1
                    DeviceImagesDisplayViewHolderType.GROUP_HEADER.type -> 4
                    else -> 1
                }
            }
        }
        mainRecyclerView.layoutManager = layoutManager
        mainRecyclerView.adapter = adapter
    }
}