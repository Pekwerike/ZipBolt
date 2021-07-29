package com.salesground.zipbolt.ui.recyclerview.directoryListDisplayFragment.navigationviewholders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.salesground.zipbolt.R
import com.salesground.zipbolt.databinding.DirectoryNavigationHeaderLayoutBinding

class DirectoryNavigationHeaderViewHolder(
    private val directoryNavigationHeaderLayoutBinding: DirectoryNavigationHeaderLayoutBinding
) : RecyclerView.ViewHolder(directoryNavigationHeaderLayoutBinding.root) {

    fun binDirectoryName(directoryName: String) {
        directoryNavigationHeaderLayoutBinding.run {
            this.directoryName = directoryName
        }
    }

    companion object {
        fun createViewHolder(parent: ViewGroup): DirectoryNavigationHeaderViewHolder {
            val layoutBinding = DataBindingUtil.inflate<DirectoryNavigationHeaderLayoutBinding>(
                LayoutInflater.from(parent.context),
                R.layout.directory_navigation_header_layout,
                parent,
                false
            )
            return DirectoryNavigationHeaderViewHolder(layoutBinding)
        }
    }
}