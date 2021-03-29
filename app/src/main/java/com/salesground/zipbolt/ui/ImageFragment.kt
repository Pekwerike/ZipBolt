package com.salesground.zipbolt.ui

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.salesground.zipbolt.databinding.FragmentImageBinding
import com.salesground.zipbolt.ui.screen.allmediadisplay.categorycontentsdisplay.images.ImagesBucketsDisplayComposable
import com.salesground.zipbolt.ui.screen.allmediadisplay.categorycontentsdisplay.images.recyclerview.DeviceImagesDisplayRecyclerViewAdapter
import com.salesground.zipbolt.ui.screen.allmediadisplay.categorycontentsdisplay.images.recyclerview.DeviceImagesDisplayViewHolderType
import com.salesground.zipbolt.ui.theme.ZipBoltTheme
import com.salesground.zipbolt.viewmodel.ImagesViewModel
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.roundToInt

class ImageFragment : Fragment() {
    private val imagesViewModel: ImagesViewModel by activityViewModels()
    private lateinit var dAdapter: DeviceImagesDisplayRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dAdapter = DeviceImagesDisplayRecyclerViewAdapter(
            onImageClicked = {
                imagesViewModel.onImageClicked(it)
            },
            imagesClicked = imagesViewModel.collectionOfClickedImages
        )
        imagesViewModel.deviceImagesGroupedByDateModified.observe(this) {
            dAdapter.submitList(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireActivity()).apply {
            setContent {
                AndroidViewBinding(FragmentImageBinding::inflate) {
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
                            dLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
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
                            dLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
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
    }
}