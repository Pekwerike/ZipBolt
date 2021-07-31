package com.salesground.zipbolt.ui.recyclerview.filesFragment.viewholders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.salesground.zipbolt.R
import com.salesground.zipbolt.databinding.FolderLayoutItemBinding
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.ui.recyclerview.DataToTransferRecyclerViewItemClickListener


class DirectoryLayoutItemViewHolder(
    private val folderLayoutItemBinding: FolderLayoutItemBinding,
    private val dataToTransferRecyclerViewItemClickListener: DataToTransferRecyclerViewItemClickListener<DataToTransfer>,
    private val folderClickedListener: DataToTransferRecyclerViewItemClickListener<String>
) : RecyclerView.ViewHolder(folderLayoutItemBinding.root) {

    fun bindData(dataToTransfer: DataToTransfer) {
        folderLayoutItemBinding.run {
            folderName = dataToTransfer.dataDisplayName

            folderLayoutItemViewGroup.setOnClickListener {
                dataToTransfer as DataToTransfer.DeviceFile
                folderClickedListener.onClick(dataToTransfer.file.path)
            }
            folderLayoutItemFolderSelectedCheckBox.setOnClickListener {
                dataToTransferRecyclerViewItemClickListener.onClick(
                    dataToTransfer
                )
            }
            executePendingBindings()
        }
    }

    companion object {
        fun createViewHolder(
            parent: ViewGroup,
            dataToTransferRecyclerViewItemClickListener: DataToTransferRecyclerViewItemClickListener<DataToTransfer>,
            folderClickedListener: DataToTransferRecyclerViewItemClickListener<String>
        ): DirectoryLayoutItemViewHolder {
            val layoutItemBinding = DataBindingUtil.inflate<FolderLayoutItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.folder_layout_item,
                parent,
                false
            )
            return DirectoryLayoutItemViewHolder(
                layoutItemBinding,
                dataToTransferRecyclerViewItemClickListener,
                folderClickedListener
            )
        }
    }
}