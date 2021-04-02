package com.salesground.zipbolt.ui

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.salesground.zipbolt.R
import com.salesground.zipbolt.databinding.FragmentImageBinding
import com.salesground.zipbolt.ui.customviews.ChipsLayout
import com.salesground.zipbolt.ui.screen.allmediadisplay.categorycontentsdisplay.images.ImagesBucketsDisplayComposable
import com.salesground.zipbolt.ui.screen.allmediadisplay.categorycontentsdisplay.images.recyclerview.DeviceImagesDisplayRecyclerViewAdapter
import com.salesground.zipbolt.ui.screen.allmediadisplay.categorycontentsdisplay.images.recyclerview.DeviceImagesDisplayViewHolderType
import com.salesground.zipbolt.ui.theme.ZipBoltTheme
import com.salesground.zipbolt.viewmodel.BucketNameAndSize
import com.salesground.zipbolt.viewmodel.ImagesViewModel
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.roundToInt

class ImageFragment : Fragment() {
    private val imagesViewModel: ImagesViewModel by activityViewModels()
    private lateinit var dAdapter: DeviceImagesDisplayRecyclerViewAdapter
    private lateinit var chipsLayout: ChipsLayout
    private var selectedCategory: BucketNameAndSize? = null

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
        imagesViewModel.deviceImagesBucketName.observe(this) {
            it?.let { it ->
                if (selectedCategory == null) selectedCategory = it.first()
                val buckets =
                    if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                        it.take(12)
                    } else it.take(20)

                buckets.forEach { bucketNameAndSize ->
                    val layout =
                        layoutInflater.inflate(R.layout.category_chip, chipsLayout, false)
                    val chip = layout.findViewById<Chip>(R.id.category_chip)
                    chip.text = when {
                        bucketNameAndSize.bucketName.length > 13 -> {
                            "${bucketNameAndSize.bucketName.take(10)}..."
                        }
                        bucketNameAndSize.bucketName.length < 4 -> {
                            " ${bucketNameAndSize.bucketName} "
                        }
                        else -> {
                            bucketNameAndSize.bucketName
                        }
                    }
                    chip.setOnClickListener {

                        chip.isChecked = true
                        if (bucketNameAndSize.bucketName != selectedCategory?.bucketName) {
                            imagesViewModel.filterDeviceImages(bucketName = bucketNameAndSize.bucketName)
                            chipsLayout.refresh(buckets.indexOf(selectedCategory))
                            selectedCategory = bucketNameAndSize
                        }
                    }
                    if (bucketNameAndSize.bucketName == selectedCategory?.bucketName) {
                        chip.isChecked = true
                    }
                    chipsLayout.addView(chip)
                }
            }
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
            chipsLayout = imagesCategoryChipsLayout
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
            return rootView.root
        }
    }
}