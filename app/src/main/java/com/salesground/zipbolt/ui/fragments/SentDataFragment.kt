package com.salesground.zipbolt.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.salesground.zipbolt.R
import com.salesground.zipbolt.viewmodel.DataToTransferViewModel


class SentDataFragment : Fragment() {

    private val dataToTransferViewModel: DataToTransferViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeViewModelLiveData()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sent_data, container, false)
    }

    private fun observeViewModelLiveData() {
        dataToTransferViewModel.sentDataItems.observe(this) {

        }
    }

}