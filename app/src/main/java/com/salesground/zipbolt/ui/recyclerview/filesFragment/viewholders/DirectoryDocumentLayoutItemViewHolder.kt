package com.salesground.zipbolt.ui.recyclerview.filesFragment.viewholders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.salesground.zipbolt.R
import com.salesground.zipbolt.databinding.FolderDocumentLayoutItemBinding
import com.salesground.zipbolt.model.DataToTransfer

class DirectoryDocumentLayoutItemViewHolder(
    private val folderDocumentLayoutItemBinding: FolderDocumentLayoutItemBinding
) : RecyclerView.ViewHolder(folderDocumentLayoutItemBinding.root) {

    fun bindData(dataToTransfer: DataToTransfer) {
        dataToTransfer as DataToTransfer.DeviceFile
        folderDocumentLayoutItemBinding.run {
            document = dataToTransfer
            executePendingBindings()
        }
    }

    companion object {
        fun createViewHolder(parent: ViewGroup): DirectoryDocumentLayoutItemViewHolder {
            val layoutBinding = DataBindingUtil.inflate<FolderDocumentLayoutItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.folder_document_layout_item,
                parent,
                false
            )
            return DirectoryDocumentLayoutItemViewHolder(layoutBinding)
        }
    }
}