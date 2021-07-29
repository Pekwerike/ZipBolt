package com.salesground.zipbolt.ui.recyclerview.directoryListDisplayFragment.navigationviewholders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.salesground.zipbolt.R
import com.salesground.zipbolt.databinding.DirectoryNavigationLayoutItemBinding

class DirectoryNavigationLayoutItemViewHolder(
    private val directoryNavigationLayoutItemBinding: DirectoryNavigationLayoutItemBinding
) : RecyclerView.ViewHolder(directoryNavigationLayoutItemBinding.root) {
    fun bindData(directoryName: String) {
        directoryNavigationLayoutItemBinding.run {
            this.directoryName = directoryName
        }
    }

    companion object {
        fun createViewHolder(parent: ViewGroup): DirectoryNavigationLayoutItemViewHolder {
            val layoutBinding = DataBindingUtil.inflate<DirectoryNavigationLayoutItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.directory_navigation_layout_item,
                parent,
                false
            )
        }
    }
}