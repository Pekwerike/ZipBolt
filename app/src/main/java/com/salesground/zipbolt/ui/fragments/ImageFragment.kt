package com.salesground.zipbolt.ui.fragments

import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.asynclayoutinflater.view.AsyncLayoutInflater
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.salesground.zipbolt.MainActivity
import com.salesground.zipbolt.R
import com.salesground.zipbolt.databinding.FragmentImageBinding
import com.salesground.zipbolt.model.ui.ImagesDisplayModel
import com.salesground.zipbolt.ui.recyclerview.imagefragment.DeviceImagesDisplayRecyclerViewAdapter
import com.salesground.zipbolt.ui.recyclerview.imagefragment.DeviceImagesDisplayViewHolderType
import com.salesground.zipbolt.viewmodel.DataToTransferViewModel
import com.salesground.zipbolt.viewmodel.ImagesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ImageFragment : Fragment() {
    private val imagesViewModel: ImagesViewModel by activityViewModels()
    private val dataToTransferViewModel: DataToTransferViewModel by activityViewModels()
    private lateinit var dAdapter: DeviceImagesDisplayRecyclerViewAdapter
    private lateinit var gridLayoutManager: GridLayoutManager
    private var mainActivity: MainActivity? = null
    private lateinit var imageCategoryChipGroup: ChipGroup
    private lateinit var imageFragmentImageBinding: FragmentImageBinding
    private lateinit var asyncLayoutInflater: AsyncLayoutInflater
    private var spanCount: Int = 3


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.let {
            mainActivity = it as MainActivity
        }
        dAdapter = DeviceImagesDisplayRecyclerViewAdapter(
            onImageClicked = {
                if (it is ImagesDisplayModel.DeviceImageDisplay) {
                    if(dataToTransferViewModel.collectionOfDataToTransfer.contains(it.deviceImage)) {
                        // remove image
                        mainActivity?.removeFromDataToTransferList(it.deviceImage)
                    }else{
                        // add image
                        mainActivity?.addToDataToTransferList(it.deviceImage)
                    }
                }
            },
            imagesClicked = dataToTransferViewModel.collectionOfDataToTransfer
        )
        spanCount = getSpanCount()
        gridLayoutManager = GridLayoutManager(context, spanCount)
        asyncLayoutInflater = AsyncLayoutInflater(requireContext())
        observeViewModelLiveData()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        imageFragmentImageBinding = FragmentImageBinding.inflate(
            inflater, null, false
        )
        return imageFragmentImageBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imageCategoryChipGroup = imageFragmentImageBinding.imagesCategoryChipsGroup

        imageFragmentImageBinding.run {
            fragmentImageRecyclerview.run {
                setHasFixedSize(true)

                gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return when (dAdapter.getItemViewType(
                            position
                        )) {
                            DeviceImagesDisplayViewHolderType.IMAGE.type -> 1
                            DeviceImagesDisplayViewHolderType.GROUP_HEADER.type -> spanCount
                            else -> 1
                        }
                    }
                }
                adapter = dAdapter
                layoutManager = gridLayoutManager
            }
        }
    }

    private fun observeViewModelLiveData() {
        imagesViewModel.run {
            deviceImagesGroupedByDateModified.observe(this@ImageFragment) {
                dAdapter.submitList(it)
            }
            deviceImagesBucketName.observe(this@ImageFragment) { it ->
                it?.let {
                    it.forEach { bucketNameAndSize ->
                        asyncLayoutInflater.inflate(
                            R.layout.category_chip,
                            imageCategoryChipGroup
                        ) { view, _, _ ->
                            view as Chip
                            view.text = bucketNameAndSize.bucketName
                            view.setOnClickListener { chip ->
                                chip as Chip
                                lifecycleScope.launch(Dispatchers.IO) {
                                    imagesViewModel.filterDeviceImages(
                                        chip.text.toString(),
                                        chip.id
                                    )
                                }
                            }
                            imageCategoryChipGroup.addView(view)
                            if (chosenBucket.first == view.text) {
                                imageCategoryChipGroup.check(view.id)
                            }
                        }
                    }
                }
            }
        }
        dataToTransferViewModel.sentDataButtonClicked.observe(this){
            it.getEvent(javaClass.name)?.let {
                dAdapter.imagesClicked = dataToTransferViewModel.collectionOfDataToTransfer
                dAdapter.notifyItemRangeChanged(
                    gridLayoutManager.findFirstVisibleItemPosition(),
                    gridLayoutManager.findLastVisibleItemPosition()
                )
            }
        }
    }

    private fun getSpanCount(): Int {
        return when (resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> {
                if (resources.displayMetrics.density > 3.1 || resources.configuration.densityDpi < 245) {
                    3
                } else {
                    4
                }
            }
            else -> {
                if (resources.displayMetrics.density > 3.1 || resources.configuration.densityDpi < 245) {
                    5
                } else {
                    7
                }
            }
        }
    }
}