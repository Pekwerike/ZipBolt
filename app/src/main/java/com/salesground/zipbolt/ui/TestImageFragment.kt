package com.salesground.zipbolt.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.salesground.zipbolt.R
import com.salesground.zipbolt.ui.screen.categorycontentsdisplay.images.DeviceImagesDisplayComposable
import com.salesground.zipbolt.ui.screen.categorycontentsdisplay.images.recyclerview.DeviceImagesDisplayRecyclerViewAdapter
import com.salesground.zipbolt.ui.screen.categorycontentsdisplay.images.recyclerview.DeviceImagesDisplayViewHolderType
import com.salesground.zipbolt.ui.theme.ZipBoltTheme
import com.salesground.zipbolt.viewmodel.ImagesViewModel

class TestImageFragment : Fragment() {
    private val imagesViewModel: ImagesViewModel by activityViewModels()
    private lateinit var dAdapter : DeviceImagesDisplayRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dAdapter = DeviceImagesDisplayRecyclerViewAdapter(
            onImageClicked = {
                imagesViewModel.onImageClicked(it)
            },
            imagesClicked = imagesViewModel.collectionOfClickedImages
        )
        imagesViewModel.deviceImagesGroupedByDateModified.observe(this){
            dAdapter.submitList(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_test_image, container, false).apply {

            findViewById<ComposeView>(R.id.compose_view_test).apply {
                setContent {
                    ZipBoltTheme {
                        DeviceImagesDisplayComposable(imagesViewModel = imagesViewModel)
                    }
                }
            }
            
            findViewById<RecyclerView>(R.id.all_device_images_recyclerview).apply {
                val dLayoutManager = GridLayoutManager(context, 4)
                dLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return when ((dAdapter as DeviceImagesDisplayRecyclerViewAdapter).getItemViewType(position)) {
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

}