package com.salesground.zipbolt.ui.recyclerview.ongoingDataTransferRecyclerViewComponents.viewHolders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.salesground.zipbolt.R
import com.salesground.zipbolt.databinding.OngoingDataTransferLayoutItemBinding
import com.salesground.zipbolt.model.DataToTransfer
import kotlin.math.roundToInt

class OngoingDataTransferViewHolder(private val ongoingDataTransferLayoutItemBinding: OngoingDataTransferLayoutItemBinding) :
    RecyclerView.ViewHolder(ongoingDataTransferLayoutItemBinding.root) {

    fun bindData(dataToTransfer: DataToTransfer) {
        ongoingDataTransferLayoutItemBinding.apply {
            dataSize = "40mb"
            dataDisplayName = dataToTransfer.dataDisplayName
            dataTransferPercent = dataToTransfer.percentTransferred.roundToInt()
            dataTransferPercentAsString = "${dataTransferPercent}%"

            Glide.with(root.context)
                .load(dataToTransfer.dataUri)
                .into(ongoingDataTransferDataCategoryImageView)

            executePendingBindings()
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