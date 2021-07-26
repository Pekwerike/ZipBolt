package com.salesground.zipbolt.ui.recyclerview.directoryListDisplayFragment.viewholders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.salesground.zipbolt.R
import com.salesground.zipbolt.databinding.FolderVideoLayoutItemBinding

class DirectoryVideoLayoutItemViewHolder(
    private val folderVideoLayoutItemBinding: FolderVideoLayoutItemBinding
) : RecyclerView.ViewHolder(folderVideoLayoutItemBinding.root) {


    companion object {
        fun createViewHolder(parent: ViewGroup): DirectoryVideoLayoutItemViewHolder {
            val layoutBinding = DataBindingUtil.inflate<FolderVideoLayoutItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.folder_video_layout_item,
                parent,
                false
            )

            return DirectoryVideoLayoutItemViewHolder(layoutBinding)
        }
    }
}