package com.salesground.zipbolt.ui.recyclerview

import androidx.recyclerview.widget.DiffUtil
import com.salesground.zipbolt.model.DataToTransfer

class DataToTransferRecyclerViewDiffUtil : DiffUtil.ItemCallback<DataToTransfer>() {
    override fun areItemsTheSame(
        oldItem: DataToTransfer,
        newItem: DataToTransfer
    ): Boolean {
        return oldItem.dataUri == newItem.dataUri
    }

    override fun areContentsTheSame(
        oldItem: DataToTransfer,
        newItem: DataToTransfer
    ): Boolean {
        return oldItem == newItem
    }
}