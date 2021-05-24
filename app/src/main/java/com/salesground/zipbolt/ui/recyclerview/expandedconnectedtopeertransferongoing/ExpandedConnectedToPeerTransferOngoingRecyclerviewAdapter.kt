package com.salesground.zipbolt.ui.recyclerview.expandedconnectedtopeertransferongoing

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.salesground.zipbolt.model.DataToTransfer

class ExpandedConnectedToPeerTransferOngoingRecyclerviewAdapter :
    ListAdapter<DataToTransfer, RecyclerView.ViewHolder>(
        ExpandedConenctedToPeerTransferOngoingRecyclerViewDiffUtil
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return OngoingDataTransferViewHolder.createViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is OngoingDataTransferViewHolder){
            holder.bindData(getItem(position))
        }
    }

    object ExpandedConenctedToPeerTransferOngoingRecyclerViewDiffUtil :
        DiffUtil.ItemCallback<DataToTransfer>() {
        override fun areItemsTheSame(oldItem: DataToTransfer, newItem: DataToTransfer): Boolean {
            return oldItem.dataUri == newItem.dataUri
        }

        override fun areContentsTheSame(oldItem: DataToTransfer, newItem: DataToTransfer): Boolean {
            return oldItem == newItem
        }
    }
}