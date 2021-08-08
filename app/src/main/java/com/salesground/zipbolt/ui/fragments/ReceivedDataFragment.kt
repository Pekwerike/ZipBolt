package com.salesground.zipbolt.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.salesground.zipbolt.R
import com.salesground.zipbolt.databinding.FragmentReceivedDataBinding
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.model.MediaType
import com.salesground.zipbolt.ui.recyclerview.receivedDataFragment.ReceivedDataFragmentRecyclerViewAdapter
import com.salesground.zipbolt.ui.recyclerview.sentDataFragment.SentDataFragmentRecyclerViewAdapter
import com.salesground.zipbolt.viewmodel.DataToTransferViewModel
import com.salesground.zipbolt.viewmodel.ReceivedDataViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToInt


@AndroidEntryPoint
class ReceivedDataFragment : Fragment() {
    private val receivedDataViewModel: ReceivedDataViewModel by activityViewModels()
    private val receivedDataFragmentRecyclerViewAdapter = ReceivedDataFragmentRecyclerViewAdapter()
    private lateinit var receivedDataFragmentLayoutManager: GridLayoutManager
    private lateinit var receivedDataFragmentBinding: FragmentReceivedDataBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeViewModelLiveData()
        receivedDataFragmentLayoutManager = GridLayoutManager(requireContext(), 3)
        receivedDataFragmentLayoutManager.spanSizeLookup =
            object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return when (receivedDataFragmentRecyclerViewAdapter.getItemViewType(
                        position
                    )) {
                        ReceivedDataFragmentRecyclerViewAdapter.ReceiveDataFragmentAdapterViewTypes.IMAGE_RECEIVE_COMPLETE.value -> 1
                        else -> 3
                    }
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        receivedDataFragmentBinding =
            FragmentReceivedDataBinding.inflate(inflater, container, false)
        return receivedDataFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        receivedDataFragmentBinding.run {
            receivedDataFragmentRecyclerview.run {
                layoutManager = receivedDataFragmentLayoutManager
                adapter = receivedDataFragmentRecyclerViewAdapter
            }
        }
    }

    private fun observeViewModelLiveData() {
        receivedDataViewModel.run {
            receivedDataItems.observe(this@ReceivedDataFragment) {
                receivedDataFragmentRecyclerViewAdapter.submitList(it)
            }
            newReceivedItemPosition.observe(this@ReceivedDataFragment) {
                if (it != -1) {
                    receivedDataFragmentRecyclerViewAdapter.notifyItemInserted(it)
                }
            }
            dataReceiveStartedDataItem.observe(this@ReceivedDataFragment) { receivedDataItem ->
                receivedDataItem?.let {
                    receivedDataFragmentBinding.receivedDataFragmentOngoingDataReceiveLayoutItem.run {
                        dataToTransfer = currentReceiveDataToTransferItem.apply {
                            dataDisplayName = it.dataDisplayName
                            dataSize = it.dataSize
                            dataType = it.dataType
                            transferStatus = DataToTransfer.TransferStatus.RECEIVE_ONGOING
                            percentTransferred = it.percentageOfDataRead
                        }
                        dataTransferPercent = it.percentageOfDataRead.roundToInt()
                    }
                }
            }
            ongoingDataReceivePercent.observe(this@ReceivedDataFragment) {
                receivedDataFragmentBinding.receivedDataFragmentOngoingDataReceiveLayoutItem.run {
                    dataTransferPercent = it.roundToInt()
                }
            }
            completedReceivedDataItem.observe(this@ReceivedDataFragment) { receivedDataItem ->
                receivedDataItem?.let {
                    receivedDataFragmentBinding.receivedDataFragmentOngoingDataReceiveLayoutItem.run {
                        dataToTransfer = it
                        dataTransferPercent = 100
                        when (it.dataType) {
                            in MediaType.File.Directory.value..
                                    MediaType.File.Document.DatDocument.value -> {
                                ongoingDataReceiveDataCategoryImageView.alpha = 0f
                                ongoingDataReceivePlainDocumentImageView.alpha = 1f
                            }

                            else -> {
                                ongoingDataReceiveDataCategoryImageView.alpha = 1f
                                ongoingDataReceivePlainDocumentImageView.alpha = 0f
                            }
                        }
                    }
                }
            }
        }
    }
}