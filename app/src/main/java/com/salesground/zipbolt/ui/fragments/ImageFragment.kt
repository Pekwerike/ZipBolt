package com.salesground.zipbolt.ui.fragments

import android.annotation.SuppressLint
import android.content.IntentFilter
import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.asynclayoutinflater.view.AsyncLayoutInflater
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.salesground.zipbolt.MainActivity
import com.salesground.zipbolt.R
import com.salesground.zipbolt.broadcast.SendDataBroadcastReceiver
import com.salesground.zipbolt.databinding.FragmentImageBinding
import com.salesground.zipbolt.model.ui.ImagesDisplayModel
import com.salesground.zipbolt.ui.recyclerview.imagefragment.DeviceImagesDisplayRecyclerViewAdapter
import com.salesground.zipbolt.ui.recyclerview.imagefragment.DeviceImagesDisplayViewHolderType
import com.salesground.zipbolt.viewmodel.ImagesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ImageFragment : Fragment() {
    private val imagesViewModel: ImagesViewModel by activityViewModels()
    private lateinit var dAdapter: DeviceImagesDisplayRecyclerViewAdapter
    private var mainActivity: MainActivity? = null
    private lateinit var imageCategoryChipGroup: ChipGroup
    private lateinit var imageFragmentImageBinding: FragmentImageBinding
    private lateinit var asyncLayoutInflater: AsyncLayoutInflater

    @Inject
    lateinit var localBroadcastManager: LocalBroadcastManager

    private val sendDataBroadcastReceiver = SendDataBroadcastReceiver(
        object : SendDataBroadcastReceiver.SendDataButtonClickedListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun sendDataButtonClicked() {
                // tell the view model to clear the collection of clicked images and notify
                // the recycler that all clicked images have been sent
                if (imagesViewModel.collectionOfClickedImages.isNotEmpty()) {
                    imagesViewModel.clearCollectionOfClickedImages()
                    dAdapter.notifyDataSetChanged()
                }
            }
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.let {
            mainActivity = it as MainActivity
        }
        dAdapter = DeviceImagesDisplayRecyclerViewAdapter(
            onImageClicked = {
                if (imagesViewModel.collectionOfClickedImages.contains(it)
                    && it is ImagesDisplayModel.DeviceImageDisplay
                ) {
                    // remove image
                    mainActivity?.removeFromDataToTransferList(it.deviceImage)
                } else if (it is ImagesDisplayModel.DeviceImageDisplay) {
                    // add image
                    mainActivity?.addToDataToTransferList(it.deviceImage)
                }
                imagesViewModel.onImageClicked(it)
            },
            imagesClicked = imagesViewModel.collectionOfClickedImages
        )
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
        imageCategoryChipGroup = imageFragmentImageBinding.imagesCategoryChipsGroup

        imageFragmentImageBinding.apply {
            fragmentImageRecyclerview.apply {
                setHasFixedSize(true)

                val spanCount: Int = when (resources.configuration.orientation) {
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
                val gridLayoutManager = GridLayoutManager(context, spanCount)
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
            return imageFragmentImageBinding.root
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
                        ) { view, resid, parent ->
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
                            if(chosenBucket.first == view.text){
                                imageCategoryChipGroup.check(view.id)
                            }
                        }
                    }
                }
            }
        }
    }


    override fun onStart() {
        super.onStart()
        localBroadcastManager.registerReceiver(sendDataBroadcastReceiver,
            IntentFilter().apply {
                addAction(SendDataBroadcastReceiver.ACTION_SEND_DATA_BUTTON_CLICKED)
            })
    }

    override fun onStop() {
        super.onStop()
        localBroadcastManager.unregisterReceiver(sendDataBroadcastReceiver)
    }
}