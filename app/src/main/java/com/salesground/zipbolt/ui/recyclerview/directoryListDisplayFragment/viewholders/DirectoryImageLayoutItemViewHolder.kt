package com.salesground.zipbolt.ui.recyclerview.directoryListDisplayFragment.viewholders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.salesground.zipbolt.R
import com.salesground.zipbolt.model.DataToTransfer
import java.io.File

class DirectoryImageLayoutItemViewHolder(
    private val folderImageLayoutItemBinding: com.salesground.zipbolt.databinding.FolderImageLayoutItemBinding
) : RecyclerView.ViewHolder(folderImageLayoutItemBinding.root) {

    fun bindData(dataToTransfer: DataToTransfer) {
        dataToTransfer as DataToTransfer.DeviceFile
        folderImageLayoutItemBinding.apply {
            this.imageFile = dataToTransfer.file

            Glide.with(root.context)
                .load(dataToTransfer.file)
                .into(folderImageLayoutItemImageView)

            folderImageLayoutItemLayoutViewGroup.setOnClickListener {
                // select image for transfer
            }

            folderImageLayoutItemFolderSelectedCheckBox.setOnClickListener {
                // select image for transfer

            }
        }
    }

    companion object {
        fun createViewHolder(parent: ViewGroup): DirectoryImageLayoutItemViewHolder {
            val layoutBinding =
                DataBindingUtil.inflate<com.salesground.zipbolt.databinding.FolderImageLayoutItemBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.folder_image_layout_item,
                    parent,
                    false
                )

            return DirectoryImageLayoutItemViewHolder(layoutBinding)
        }
    }
}