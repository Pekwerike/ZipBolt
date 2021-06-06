package com.salesground.zipbolt.ui.recyclerview.applicationFragment

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.salesground.zipbolt.model.DataToTransfer

class ApplicationFragmentAppsDisplayRecyclerViewAdapter :
    ListAdapter<DataToTransfer, RecyclerView.ViewHolder>(
        ApplicationFragmentAppsDisplayRecyclerViewAdapterDiffUtil
    ) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ApplicationLayoutItemViewHolder.createViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is ApplicationLayoutItemViewHolder){
            holder.bindApplicationDetails(getItem(position))
        }
    }

    object ApplicationFragmentAppsDisplayRecyclerViewAdapterDiffUtil :
        DiffUtil.ItemCallback<DataToTransfer>() {
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

}