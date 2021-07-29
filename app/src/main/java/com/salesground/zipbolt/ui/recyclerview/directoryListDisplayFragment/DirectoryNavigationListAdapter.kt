package com.salesground.zipbolt.ui.recyclerview.directoryListDisplayFragment

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.salesground.zipbolt.ui.recyclerview.directoryListDisplayFragment.navigationviewholders.DirectoryNavigationHeaderViewHolder
import com.salesground.zipbolt.ui.recyclerview.directoryListDisplayFragment.navigationviewholders.DirectoryNavigationLayoutItemViewHolder

class DirectoryNavigationListAdapter : ListAdapter<String, RecyclerView.ViewHolder>(
    DirectoryNavigationListDiffUtil()
) {
    companion object {
        const val ROOT_HEADER = 1
        const val NORMAL_HEADER = 2
        const val ENDING_HEADER = 3
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            ROOT_HEADER
        } else {
            NORMAL_HEADER
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ROOT_HEADER -> DirectoryNavigationHeaderViewHolder.createViewHolder(parent)
            NORMAL_HEADER -> DirectoryNavigationLayoutItemViewHolder.createViewHolder(parent)
            else -> DirectoryNavigationLayoutItemViewHolder.createViewHolder(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DirectoryNavigationLayoutItemViewHolder -> {
                holder.bindData(getItem(position))
            }
            is DirectoryNavigationHeaderViewHolder -> {
                holder.binDirectoryName(getItem(position))
            }
        }
    }
}


class DirectoryNavigationListDiffUtil : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

}