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
import com.salesground.zipbolt.ui.recyclerview.receivedDataFragment.ReceivedDataFragmentRecyclerViewAdapter
import com.salesground.zipbolt.ui.recyclerview.sentDataFragment.SentDataFragmentRecyclerViewAdapter
import com.salesground.zipbolt.viewmodel.DataToTransferViewModel
import com.salesground.zipbolt.viewmodel.ReceivedDataViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ReceivedDataFragment : Fragment() {

    private val receivedDataViewModel: ReceivedDataViewModel by activityViewModels()
    private val receivedDataFragmentRecyclerViewAdapter = ReceivedDataFragmentRecyclerViewAdapter()
    private lateinit var receivedDataFragmentLayoutManager : GridLayoutManager
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
        receivedDataViewModel.receivedDataItems.observe(this) {
            receivedDataFragmentRecyclerViewAdapter.submitList(it)
        }
    }
}