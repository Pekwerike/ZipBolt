package com.salesground.zipbolt.ui.recyclerview.filesFragment.viewholders

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.salesground.zipbolt.R
import com.salesground.zipbolt.databinding.FolderAppLayoutItemBinding
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.ui.customviews.SelectableLinearLayout
import com.salesground.zipbolt.ui.recyclerview.DataToTransferRecyclerViewItemClickListener

class DirectoryAppLayoutItemViewHolder(
    private val folderAppLayoutItemBinding: FolderAppLayoutItemBinding,
    private val dataToTransferRecyclerViewItemClickListener: DataToTransferRecyclerViewItemClickListener<DataToTransfer>
) : RecyclerView.ViewHolder(folderAppLayoutItemBinding.root) {

    fun bindData(
        dataToTransfer: DataToTransfer,
        filesSelectedForTransfer: MutableList<DataToTransfer>
    ) {
        dataToTransfer as DataToTransfer.DeviceFile

        folderAppLayoutItemBinding.run {
            appFile = dataToTransfer.file

            isItemSelected(
                folderAppLayoutItemLayoutViewGroup,
                folderAppLayoutItemFolderSelectedCheckBox,
                filesSelectedForTransfer.contains(dataToTransfer)
            )


            folderAppLayoutItemLayoutViewGroup.setOnClickListener {
                isItemSelected(
                    folderAppLayoutItemLayoutViewGroup,
                    folderAppLayoutItemFolderSelectedCheckBox,
                    !filesSelectedForTransfer.contains(dataToTransfer)
                )
                dataToTransferRecyclerViewItemClickListener.onClick(dataToTransfer)

            }
            folderAppLayoutItemFolderSelectedCheckBox.setOnClickListener {
                isItemSelected(
                    folderAppLayoutItemLayoutViewGroup,
                    folderAppLayoutItemFolderSelectedCheckBox,
                    !filesSelectedForTransfer.contains(dataToTransfer)
                )
                dataToTransferRecyclerViewItemClickListener.onClick(dataToTransfer)
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
            dataToTransferRecyclerViewItemClickListener: DataToTransferRecyclerViewItemClickListener<DataToTransfer>
        ): DirectoryAppLayoutItemViewHolder {
            val layoutItemBinding = DataBindingUtil.inflate<FolderAppLayoutItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.folder_app_layout_item,
                parent,
                false
            )
            return DirectoryAppLayoutItemViewHolder(
                layoutItemBinding,
                dataToTransferRecyclerViewItemClickListener
            )
        }
    }
}