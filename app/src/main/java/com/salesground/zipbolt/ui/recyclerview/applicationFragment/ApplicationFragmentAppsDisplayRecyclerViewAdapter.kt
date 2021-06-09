package com.salesground.zipbolt.ui.recyclerview.applicationFragment

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.ui.recyclerview.DataToTransferRecyclerViewItemClickListener

class ApplicationFragmentAppsDisplayRecyclerViewAdapter(
    private val dataToTransferRecyclerViewItemClickListener:
    DataToTransferRecyclerViewItemClickListener,
    private val selectedApplications: MutableList<DataToTransfer>
) : ListAdapter<DataToTransfer, RecyclerView.ViewHolder>(
    ApplicationFragmentAppsDisplayRecyclerViewAdapterDiffUtil
) {
    enum class AdapterViewTypes(val value: Int) {
        NORMAL(1),
        EXPANDED(2)
    }

    override fun getItemViewType(position: Int): Int {
        val currentItemNameLength = getItem(position).dataDisplayName.length
        return if (currentItemNameLength < 15) {
            AdapterViewTypes.NORMAL.value
        } else {
            AdapterViewTypes.EXPANDED.value
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            AdapterViewTypes.NORMAL.value -> {
                ApplicationLayoutItemViewHolder.createViewHolder(
                    parent,
                    dataToTransferRecyclerViewItemClickListener
                )
            }
            else -> {
                ApplicationLayoutItemExpandedViewHolder.createViewHolder(
                    parent,
                    dataToTransferRecyclerViewItemClickListener
                )
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ApplicationLayoutItemViewHolder) {
            holder.bindApplicationDetails(
                getItem(position),
                selectedApplications
            )
        } else if (holder is ApplicationLayoutItemExpandedViewHolder) {
            holder.bindAppData(
                getItem(position),
                selectedApplications
            )
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

