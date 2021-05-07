package com.salesground.zipbolt.ui.recyclerview.expandedconnectedtopeertransferongoing

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.salesground.zipbolt.R
import com.salesground.zipbolt.databinding.OngoingDataTransferLayoutItemBinding

class OngoingDataTransferViewHolder(private val ongoingDataTransferLayoutItemBinding: OngoingDataTransferLayoutItemBinding) :
    RecyclerView.ViewHolder(ongoingDataTransferLayoutItemBinding.root) {

    fun bindData(){
        ongoingDataTransferLayoutItemBinding.apply {
            dataDisplayName = "Replace oo"
            dataPercentState = 908
        }
    }
    companion object {
        fun createViewHolder(parent: ViewGroup): OngoingDataTransferViewHolder {
            val layoutItemBinding = DataBindingUtil.inflate<OngoingDataTransferLayoutItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.ongoing_data_transfer_layout_item,
                parent,
                false
            )
            return OngoingDataTransferViewHolder(layoutItemBinding)
        }
    }
}