package com.salesground.zipbolt.ui.screen.allmediadisplay.categorycontentsdisplay

import android.content.res.Configuration
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.recyclerview.widget.GridLayoutManager
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.salesground.zipbolt.databinding.FragmentImageBinding
import com.salesground.zipbolt.ui.screen.allmediadisplay.categorycontentsdisplay.images.ImagesBucketsDisplayComposable
import com.salesground.zipbolt.ui.screen.allmediadisplay.categorycontentsdisplay.images.recyclerview.DeviceImagesDisplayRecyclerViewAdapter
import com.salesground.zipbolt.ui.screen.allmediadisplay.categorycontentsdisplay.images.recyclerview.DeviceImagesDisplayViewHolderType
import com.salesground.zipbolt.ui.theme.ZipBoltTheme
import com.salesground.zipbolt.viewmodel.ImagesViewModel
import kotlinx.coroutines.launch

@ExperimentalPagerApi
@Composable
fun AllMediaOnDeviceComposable(imagesViewModel: ImagesViewModel) {
    val deviceImages = imagesViewModel.deviceImagesGroupedByDateModified.observeAsState()
    val scope = rememberCoroutineScope()
    val pages = remember {
        listOf("Apps", "Images", "Videos", "Music", "Files")
    }
    val pagerState = rememberPagerState(pageCount = pages.size)


    Column(modifier = Modifier.fillMaxSize()) {

        TabRow(selectedTabIndex = pagerState.currentPage,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
                )
            }) {
            pages.forEachIndexed { index, pageTitle ->
                Tab(
                    text = { Text(pageTitle, style = MaterialTheme.typography.body2) },
                    selected = pagerState.currentPage == index,
                    onClick = {
                        // Animate to the selected page when clicked
                        scope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    }
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
            offscreenLimit = 4,

            ) { page ->
            Box(modifier = Modifier.fillMaxSize()) {
                when (page) {
                    0 -> {
                        Text(text = "Hello 0")
                    }
                    1 -> {
                        key(1){
                        AndroidViewBinding(FragmentImageBinding::inflate) {

                            val dAdapter = DeviceImagesDisplayRecyclerViewAdapter(
                                onImageClicked = {
                                    imagesViewModel.onImageClicked(it)
                                },
                                imagesClicked = imagesViewModel.collectionOfClickedImages
                            )
                            dAdapter.submitList(deviceImages.value)

                            fragmentImageImagesBucketViewGroup.apply {
                                setContent {
                                    ZipBoltTheme {
                                        ImagesBucketsDisplayComposable(imagesViewModel = imagesViewModel)
                                    }
                                }
                            }
                            fragmentImageRecyclerview.apply {

                                if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                                    val dLayoutManager = GridLayoutManager(context, 4)
                                    dLayoutManager.spanSizeLookup =
                                        object : GridLayoutManager.SpanSizeLookup() {
                                            override fun getSpanSize(position: Int): Int {
                                                return when (dAdapter.getItemViewType(
                                                    position
                                                )) {
                                                    DeviceImagesDisplayViewHolderType.IMAGE.type -> 1
                                                    DeviceImagesDisplayViewHolderType.GROUP_HEADER.type -> 4
                                                    else -> 1
                                                }
                                            }
                                        }
                                    // mainRecyclerView.isNestedScrollingEnabled = true
                                    this.adapter = dAdapter
                                    this.layoutManager = dLayoutManager
                                } else {
                                    val dLayoutManager = GridLayoutManager(context, 6)
                                    dLayoutManager.spanSizeLookup =
                                        object : GridLayoutManager.SpanSizeLookup() {
                                            override fun getSpanSize(position: Int): Int {
                                                return when (dAdapter.getItemViewType(
                                                    position
                                                )) {
                                                    DeviceImagesDisplayViewHolderType.IMAGE.type -> 1
                                                    DeviceImagesDisplayViewHolderType.GROUP_HEADER.type -> 6
                                                    else -> 1
                                                }
                                            }
                                        }
                                    // mainRecyclerView.isNestedScrollingEnabled = true
                                    this.adapter = dAdapter
                                    this.layoutManager = dLayoutManager
                                }
                            }
                        }
                        }
                    }
                    2 -> {
                        Text(text = "Hello 3")
                    }
                    3 -> {
                        Text(text = "Hello 3")
                    }
                    4 -> {
                        Text(text = "Hello 4")
                    }
                }
            }
        }
    }
}

/**
 * This indicator syncs up the tab indicator with the [HorizontalPager] position.
 * We may add this in the library at some point.
 */
@OptIn(ExperimentalPagerApi::class)
fun Modifier.pagerTabIndicatorOffset(
    pagerState: PagerState,
    tabPositions: List<TabPosition>,
): Modifier = composed {
    val targetIndicatorOffset: Dp
    val indicatorWidth: Dp

    val currentTab = tabPositions[pagerState.currentPage]
    val nextTab = tabPositions.getOrNull(pagerState.currentPage + 1)
    if (nextTab != null) {
        // If we have a next tab, lerp between the size and offset
        targetIndicatorOffset = lerp(currentTab.left, nextTab.left, pagerState.currentPageOffset)
        indicatorWidth = lerp(currentTab.width, nextTab.width, pagerState.currentPageOffset)
    } else {
        // Otherwise we just use the current tab/page
        targetIndicatorOffset = currentTab.left
        indicatorWidth = currentTab.width
    }

    fillMaxWidth()
        .wrapContentSize(Alignment.BottomStart)
        .offset(x = targetIndicatorOffset)
        .width(indicatorWidth)
}