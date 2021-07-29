package com.salesground.zipbolt.ui.recyclerview.directoryListDisplayFragment.navigationviewholders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.salesground.zipbolt.R
import com.salesground.zipbolt.databinding.DirectoryNavigationLayoutItemBinding
import com.salesground.zipbolt.ui.recyclerview.DataToTransferRecyclerViewItemClickListener
import java.io.File

class DirectoryNavigationLayoutItemViewHolder(
    private val directoryNavigationLayoutItemBinding: DirectoryNavigationLayoutItemBinding,
    private val dataToTransferRecyclerViewItemClickListener: DataToTransferRecyclerViewItemClickListener<String>
) : RecyclerView.ViewHolder(directoryNavigationLayoutItemBinding.root) {
    fun bindData(directoryName: String) {
        directoryNavigationLayoutItemBinding.run {
            this.directoryName = directoryName
            directoryNavigationLayoutItemDirectoryTextView.setOnClickListener {
                dataToTransferRecyclerViewItemClickListener.onClick(directoryName)
            }
            executePendingBindings()
        }
    }

    companion object {
        fun createViewHolder(
            parent: ViewGroup,
            dataToTransferRecyclerViewItemClickListener: DataToTransferRecyclerViewItemClickListener<String>
        ): DirectoryNavigationLayoutItemViewHolder {
            val layoutBinding = DataBindingUtil.inflate<DirectoryNavigationLayoutItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.directory_navigation_layout_item,
                parent,
                false
            )

            return DirectoryNavigationLayoutItemViewHolder(layoutBinding, dataToTransferRecyclerViewItemClickListener)
        }
    }
}