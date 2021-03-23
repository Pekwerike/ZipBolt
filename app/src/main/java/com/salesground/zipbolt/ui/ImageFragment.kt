package com.salesground.zipbolt.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.salesground.zipbolt.databinding.FragmentImageBinding
import com.salesground.zipbolt.ui.screen.categorycontentsdisplay.images.ImagesBucketDisplayComposable
import com.salesground.zipbolt.ui.screen.categorycontentsdisplay.images.recyclerview.DeviceImagesDisplayRecyclerViewAdapter
import com.salesground.zipbolt.ui.screen.categorycontentsdisplay.images.recyclerview.DeviceImagesDisplayViewHolderType
import com.salesground.zipbolt.ui.theme.ZipBoltTheme
import com.salesground.zipbolt.viewmodel.ImagesViewModel

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
        val rootView = FragmentImageBinding.inflate(
            inflater, container,
            false
        )
        rootView.apply {
            composeViewTest.apply {
                setContent {
                    ZipBoltTheme {
                        ImagesBucketDisplayComposable(imagesViewModel = imagesViewModel)
                    }
                }
            }

            allDeviceImagesRecyclerview.apply {
                mainRecyclerView.apply {
                    val dLayoutManager = GridLayoutManager(context, 4)
                    dLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int {
                            return when ((dAdapter as DeviceImagesDisplayRecyclerViewAdapter).getItemViewType(
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
                }
            }
        }

        return rootView.root
    }

}