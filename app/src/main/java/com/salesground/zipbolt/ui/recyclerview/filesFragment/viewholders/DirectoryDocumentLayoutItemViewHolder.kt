package com.salesground.zipbolt.ui.recyclerview.filesFragment.viewholders

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.salesground.zipbolt.R
import com.salesground.zipbolt.databinding.FolderDocumentLayoutItemBinding
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.ui.customviews.SelectableLinearLayout
import com.salesground.zipbolt.ui.recyclerview.RecyclerViewItemClickedListener

class DirectoryDocumentLayoutItemViewHolder(
    private val folderDocumentLayoutItemBinding: FolderDocumentLayoutItemBinding,
    private val directoryDocumentLayoutClickedListener: RecyclerViewItemClickedListener<DataToTransfer>
) : RecyclerView.ViewHolder(folderDocumentLayoutItemBinding.root) {

    fun bindData(
        dataToTransfer: DataToTransfer,
        filesSelectedForTransfer: MutableList<DataToTransfer>
    ) {
        dataToTransfer as DataToTransfer.DeviceFile
        folderDocumentLayoutItemBinding.run {
            document = dataToTransfer

            isItemSelected(
                folderDocumentLayoutItemLayoutViewGroup,
                folderDocumentLayoutItemFolderSelectedCheckBox,
                filesSelectedForTransfer.contains(dataToTransfer)
            )


            folderDocumentLayoutItemLayoutViewGroup.setOnClickListener {
                isItemSelected(
                    folderDocumentLayoutItemLayoutViewGroup,
                    folderDocumentLayoutItemFolderSelectedCheckBox,
                    !filesSelectedForTransfer.contains(dataToTransfer)
                )
                directoryDocumentLayoutClickedListener.onClick(dataToTransfer)
            }

            folderDocumentLayoutItemFolderSelectedCheckBox.setOnClickListener {
                isItemSelected(
                    folderDocumentLayoutItemLayoutViewGroup,
                    folderDocumentLayoutItemFolderSelectedCheckBox,
                    !filesSelectedForTransfer.contains(dataToTransfer)
                )
                directoryDocumentLayoutClickedListener.onClick(dataToTransfer)
            }
            executePendingBindings()
        }
    }

    private fun isItemSelected(
        viewGroup: SelectableLinearLayout,
        selectedCheckBox: CheckBox,
        isSelected: Boolean
    ) {
        viewGroup.setIsViewSelected(isSelected)
        selectedCheckBox.isChecked = isSelected
    }

    companion object {
        fun createViewHolder(
            parent: ViewGroup,
            directoryDocumentLayoutClickedListener: RecyclerViewItemClickedListener<DataToTransfer>
        ): DirectoryDocumentLayoutItemViewHolder {
            val layoutBinding = DataBindingUtil.inflate<FolderDocumentLayoutItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.folder_document_layout_item,
                parent,
                false
            )
            return DirectoryDocumentLayoutItemViewHolder(
                layoutBinding,
                directoryDocumentLayoutClickedListener
            )
        }
    }
}