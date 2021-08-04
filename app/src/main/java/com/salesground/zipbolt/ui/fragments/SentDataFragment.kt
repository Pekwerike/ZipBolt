package com.salesground.zipbolt.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.salesground.zipbolt.R
import com.salesground.zipbolt.databinding.FragmentSentDataBinding
import com.salesground.zipbolt.ui.recyclerview.sentDataFragment.SentDataFragmentRecyclerViewAdapter
import com.salesground.zipbolt.viewmodel.DataToTransferViewModel
import com.salesground.zipbolt.viewmodel.SentDataViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SentDataFragment : Fragment() {
    private lateinit var sentDataFragmentBinding: FragmentSentDataBinding
    private val sentDataViewModel: SentDataViewModel by activityViewModels()
    private val sentDataFragmentRecyclerViewAdapter = SentDataFragmentRecyclerViewAdapter()
    private lateinit var sentDataFragmentLayoutManager: GridLayoutManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeViewModelLiveData()
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
            sentDataFragmentRecyclerview.post {
                sentDataFragmentRecyclerview.run {
                    sentDataFragmentLayoutManager = GridLayoutManager(requireContext(), 3)
                  /*  sentDataFragmentLayoutManager.spanSizeLookup =
                        object : GridLayoutManager.SpanSizeLookup() {
                            override fun getSpanSize(position: Int): Int {
                              when (sentDataFragmentRecyclerViewAdapter.getItemViewType(
                                    position
                                )) {
                                    SentDataFragmentRecyclerViewAdapter.SentDataFragmentAdapterViewTypes.IMAGE_TRANSFER_WAITING.value -> 1
                                    SentDataFragmentRecyclerViewAdapter.SentDataFragmentAdapterViewTypes.IMAGE_TRANSFER_COMPLETE.value -> 1
                                    else -> 3
                                }
                            }
                        }*/
                    layoutManager = sentDataFragmentLayoutManager
                    adapter = sentDataFragmentRecyclerViewAdapter
                }
            }
        }
    }

    private fun observeViewModelLiveData() {
        sentDataViewModel.run {
            sentDataItems.observe(this@SentDataFragment) {
                sentDataFragmentRecyclerViewAdapter.submitList(it)
            }
            canceledSentDataItemIndex.observe(this@SentDataFragment) {
                sentDataFragmentRecyclerViewAdapter.submitList(sentDataItems.value)
                sentDataFragmentRecyclerViewAdapter.notifyItemRemoved(it)
            }
            updatedSentDataItemIndex.observe(this@SentDataFragment) {
                sentDataFragmentRecyclerViewAdapter.submitList(sentDataItems.value)
                sentDataFragmentRecyclerViewAdapter.notifyItemChanged(it)
            }
        }
    }

}