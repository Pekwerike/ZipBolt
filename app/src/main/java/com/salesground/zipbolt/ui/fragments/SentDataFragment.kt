package com.salesground.zipbolt.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.salesground.zipbolt.MainActivity
import com.salesground.zipbolt.databinding.FragmentSentDataBinding
import com.salesground.zipbolt.model.MediaType
import com.salesground.zipbolt.ui.recyclerview.sentDataFragment.SentDataFragmentRecyclerViewAdapter
import com.salesground.zipbolt.viewmodel.SentDataViewModel


class SentDataFragment : Fragment() {
    private lateinit var sentDataFragmentBinding: FragmentSentDataBinding
    private val sentDataViewModel: SentDataViewModel by activityViewModels()
    private val sentDataFragmentRecyclerViewAdapter = SentDataFragmentRecyclerViewAdapter()
    private lateinit var sentDataFragmentLayoutManager: GridLayoutManager
    private var mainActivity: MainActivity? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.let {
            mainActivity = it as MainActivity
        }
        observeViewModelLiveData()
        sentDataFragmentLayoutManager = GridLayoutManager(requireContext(), 3)
        sentDataFragmentLayoutManager.spanSizeLookup =
            object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return when (sentDataFragmentRecyclerViewAdapter.getItemViewType(
                        position
                    )) {
                        SentDataFragmentRecyclerViewAdapter.SentDataFragmentAdapterViewTypes.IMAGE_TRANSFER_WAITING.value -> 1
                        SentDataFragmentRecyclerViewAdapter.SentDataFragmentAdapterViewTypes.IMAGE_TRANSFER_COMPLETE.value -> 1
                        else -> 3
                    }
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sentDataFragmentBinding = FragmentSentDataBinding.inflate(
            inflater, container, false
        )
        // Inflate the layout for this fragment
        return sentDataFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        sentDataFragmentBinding.run {
            root.isNestedScrollingEnabled = true
            sentDataFragmentRecyclerview.run {
                isNestedScrollingEnabled = true
                layoutManager = sentDataFragmentLayoutManager
                adapter = sentDataFragmentRecyclerViewAdapter

                sentDataFragmentRecyclerview.post {
                    sentDataFragmentRecyclerview.run {
                        layoutManager = sentDataFragmentLayoutManager
                        adapter = sentDataFragmentRecyclerViewAdapter
                    }
                }
            }
            sentDataFragmentOngoingDataTransferLayoutItem.run {
                ongoingDataTransferLayoutCancelTransferImageButton.setOnClickListener {
                    mainActivity?.cancelOngoingDataTransfer()
                }
            }
        }
    }

    private fun observeViewModelLiveData() {
        sentDataViewModel.run {
            sentDataItems.observe(this@SentDataFragment) {
                sentDataFragmentRecyclerViewAdapter.submitList(it)
                sentDataFragmentRecyclerViewAdapter.notifyDataSetChanged()
            }

            updatedSentDataItemIndex.observe(this@SentDataFragment) {
                sentDataFragmentRecyclerViewAdapter.submitList(sentDataItems.value)
                sentDataFragmentRecyclerViewAdapter.notifyItemChanged(it)
            }

            currentDataToTransferDataItem.observe(this@SentDataFragment) { dataToTransfer ->
                dataToTransfer?.let {
                    sentDataFragmentBinding.sentDataFragmentOngoingDataTransferLayoutItem.run {
                        this.dataToTransfer = dataToTransfer
                        when (dataToTransfer.dataType) {
                            in MediaType.File.Directory.value..
                                    MediaType.File.Document.DatDocument.value -> {
                                ongoingDataTransferDataCategoryImageView.alpha = 0f
                                ongoingDataTransferPlainDocumentOrDirectoryImageView.alpha = 1f
                            }
                            else -> {
                                ongoingDataTransferDataCategoryImageView.alpha = 1f
                                ongoingDataTransferPlainDocumentOrDirectoryImageView.alpha = 0f
                            }
                        }
                    }
                }
            }
            currentDataToTransferPercentTransferred.observe(this@SentDataFragment) {
                sentDataFragmentBinding.sentDataFragmentOngoingDataTransferLayoutItem.run {
                    this.dataTransferPercent = it
                }
            }
        }
    }
}