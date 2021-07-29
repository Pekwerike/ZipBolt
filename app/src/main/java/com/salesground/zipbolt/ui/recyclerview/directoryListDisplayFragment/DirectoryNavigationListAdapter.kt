package com.salesground.zipbolt.ui.recyclerview.directoryListDisplayFragment

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.salesground.zipbolt.ui.recyclerview.DataToTransferRecyclerViewItemClickListener
import com.salesground.zipbolt.ui.recyclerview.directoryListDisplayFragment.navigationviewholders.DirectoryNavigationHeaderViewHolder
import com.salesground.zipbolt.ui.recyclerview.directoryListDisplayFragment.navigationviewholders.DirectoryNavigationLayoutItemViewHolder
import com.salesground.zipbolt.ui.recyclerview.directoryListDisplayFragment.navigationviewholders.DirectoryNavigationTailViewHolder
import java.io.File

class DirectoryNavigationListAdapter(
    private val dataToTransferRecyclerViewItemClickListener: DataToTransferRecyclerViewItemClickListener<String>
) : ListAdapter<File, RecyclerView.ViewHolder>(
    DirectoryNavigationListDiffUtil()) {
    companion object {
        const val ROOT_HEADER = 1
        const val NORMAL_HEADER = 2
        const val ENDING_HEADER = 3
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0 && itemCount - 1 == 0) {
            ENDING_HEADER
        } else if (position == 0) {
            NORMAL_HEADER
        } else if (position == itemCount - 1) {
            ENDING_HEADER
        } else {
            NORMAL_HEADER
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            NORMAL_HEADER -> DirectoryNavigationLayoutItemViewHolder.createViewHolder(parent, dataToTransferRecyclerViewItemClickListener)
            ENDING_HEADER -> DirectoryNavigationTailViewHolder.createViewHolder(parent)
            else -> DirectoryNavigationLayoutItemViewHolder.createViewHolder(parent, dataToTransferRecyclerViewItemClickListener)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DirectoryNavigationLayoutItemViewHolder -> {
                holder.bindData(getItem(position).name)
            }
            is DirectoryNavigationHeaderViewHolder -> {
                holder.bindData(getItem(position).name)
            }
            is DirectoryNavigationTailViewHolder -> {
                holder.bindData(getItem(position).name)
            }
        }
    }
}


class DirectoryNavigationListDiffUtil : DiffUtil.ItemCallback<File>() {
    override fun areItemsTheSame(oldItem: File, newItem: File): Boolean {
        return oldItem.absolutePath == newItem.absolutePath
    }

    override fun areContentsTheSame(oldItem: File, newItem: File): Boolean {
        return oldItem == newItem
    }
}