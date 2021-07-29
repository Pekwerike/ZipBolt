package com.salesground.zipbolt.ui.recyclerview.applicationFragment

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.ui.recyclerview.DataToTransferDiffUtil
import com.salesground.zipbolt.ui.recyclerview.DataToTransferRecyclerViewItemClickListener

class ApplicationFragmentAppsDisplayRecyclerViewAdapter(
    private val dataToTransferRecyclerViewItemClickListener:
    DataToTransferRecyclerViewItemClickListener<DataToTransfer>,
    private val selectedApplications: MutableList<DataToTransfer>
) : ListAdapter<DataToTransfer, RecyclerView.ViewHolder>(
    DataToTransferDiffUtil()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ApplicationLayoutItemViewHolder.createViewHolder(
            parent,
            dataToTransferRecyclerViewItemClickListener
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ApplicationLayoutItemViewHolder) {
            val currentItem = getItem(position)
            holder.bindApplicationDetails(
                currentItem,
                selectedApplications
            )
        }
    }

}

