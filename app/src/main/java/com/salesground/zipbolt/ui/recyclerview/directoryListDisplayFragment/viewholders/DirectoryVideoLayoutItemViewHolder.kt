package com.salesground.zipbolt.ui.recyclerview.directoryListDisplayFragment.viewholders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.salesground.zipbolt.R
import com.salesground.zipbolt.databinding.FolderVideoLayoutItemBinding
import com.salesground.zipbolt.model.DataToTransfer

class DirectoryVideoLayoutItemViewHolder(
    private val folderVideoLayoutItemBinding: FolderVideoLayoutItemBinding
) : RecyclerView.ViewHolder(folderVideoLayoutItemBinding.root) {


    fun bindData(dataToTransfer: DataToTransfer) {
        dataToTransfer as DataToTransfer.DeviceFile
        folderVideoLayoutItemBinding.apply {
            videoFile = dataToTransfer.file

            Glide.with(root.context)
                .load(dataToTransfer.file)
                .into(folderVideoLayoutItemImageView)

            folderVideoLayoutItemLayoutViewGroup.setOnClickListener {

            }
            folderVideoLayoutItemFolderSelectedCheckBox.setOnClickListener {

            }
        }
    }

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