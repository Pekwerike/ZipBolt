package com.salesground.zipbolt.ui.recyclerview.expandedconnectedtopeertransferongoing

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.salesground.zipbolt.R
import com.salesground.zipbolt.databinding.OngoingDataTransferLayoutItemBinding
import com.salesground.zipbolt.model.DataToTransfer
import kotlin.math.roundToInt

class OngoingDataTransferViewHolder(private val ongoingDataTransferLayoutItemBinding: OngoingDataTransferLayoutItemBinding) :
    RecyclerView.ViewHolder(ongoingDataTransferLayoutItemBinding.root) {

    fun bindData(dataToTransfer: DataToTransfer) {
        ongoingDataTransferLayoutItemBinding.apply {
            dataDisplayName = dataToTransfer.dataDisplayName
            dataTransferPercent = dataToTransfer.percentTransfered.roundToInt()
            dataTransferPercentAsString = "${dataTransferPercent}%"
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