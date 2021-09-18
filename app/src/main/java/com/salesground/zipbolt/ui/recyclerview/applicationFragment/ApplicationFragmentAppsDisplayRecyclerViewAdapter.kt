package com.salesground.zipbolt.ui.recyclerview.applicationFragment

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.ui.recyclerview.DataToTransferRecyclerViewDiffUtil
import com.salesground.zipbolt.ui.recyclerview.RecyclerViewItemClickedListener

class ApplicationFragmentAppsDisplayRecyclerViewAdapter(
    private val applicationLayoutClickedListener:
    RecyclerViewItemClickedListener<DataToTransfer>,
    var  selectedApplications: MutableList<DataToTransfer>
) : ListAdapter<DataToTransfer, RecyclerView.ViewHolder>(
    DataToTransferRecyclerViewDiffUtil()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ApplicationLayoutItemViewHolder.createViewHolder(
            parent,
            applicationLayoutClickedListener
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

