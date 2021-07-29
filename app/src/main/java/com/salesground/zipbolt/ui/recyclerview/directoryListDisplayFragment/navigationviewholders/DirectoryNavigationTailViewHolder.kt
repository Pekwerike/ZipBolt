package com.salesground.zipbolt.ui.recyclerview.directoryListDisplayFragment.navigationviewholders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.salesground.zipbolt.R
import com.salesground.zipbolt.databinding.DirectoryNavigationTailLayoutItemBinding
import java.io.File

class DirectoryNavigationTailViewHolder(
    private val directoryNavigationTailLayoutItemBinding: DirectoryNavigationTailLayoutItemBinding
) : RecyclerView.ViewHolder(directoryNavigationTailLayoutItemBinding.root) {

    fun bindData(directoryPath: String){
        directoryNavigationTailLayoutItemBinding.run{
            this.directoryName = directoryPath
            executePendingBindings()
        }
    }
    companion object {
        fun createViewHolder(parent: ViewGroup): DirectoryNavigationTailViewHolder {
            val layoutBinding = DataBindingUtil.inflate<DirectoryNavigationTailLayoutItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.directory_navigation_tail_layout_item,
                parent,
                false
            )
            return DirectoryNavigationTailViewHolder(layoutBinding)
        }
    }
}