package com.salesground.zipbolt.ui.recyclerview.ongoingDataTransferRecyclerViewComponents.viewHolders.directory

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.salesground.zipbolt.R
import com.salesground.zipbolt.databinding.FolderTransferLayoutItemBinding
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.utils.getDirectorySize

class DirectoryTransferCompleteLayoutItemViewHolder(
    private val folderTransferLayoutItemBinding: FolderTransferLayoutItemBinding
) : RecyclerView.ViewHolder(folderTransferLayoutItemBinding.root) {

    fun bindData(dataToTransfer: DataToTransfer) {
        dataToTransfer as DataToTransfer.DeviceFile
        folderTransferLayoutItemBinding.run {
            folderSize = dataToTransfer.file.getDirectorySize()
            folderName = dataToTransfer.dataDisplayName
            folderTransferLayoutItemShimmer.run {
                stopShimmer()
                hideShimmer()
            }
        }
    }

    companion object {
        fun createViewHolder(parent: ViewGroup): DirectoryTransferCompleteLayoutItemViewHolder {
            val layoutBinding = DataBindingUtil.inflate<FolderTransferLayoutItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.folder_transfer_layout_item,
                parent,
                false
            )
            return DirectoryTransferCompleteLayoutItemViewHolder(layoutBinding)
        }
    }
}