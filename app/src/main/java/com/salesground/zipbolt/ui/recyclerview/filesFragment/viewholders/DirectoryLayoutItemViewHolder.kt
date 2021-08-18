package com.salesground.zipbolt.ui.recyclerview.filesFragment.viewholders

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.salesground.zipbolt.R
import com.salesground.zipbolt.databinding.FolderLayoutItemBinding
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.ui.customviews.SelectableLinearLayout
import com.salesground.zipbolt.ui.recyclerview.DataToTransferRecyclerViewItemClickListener
import com.salesground.zipbolt.utils.getDirectorySize


class DirectoryLayoutItemViewHolder(
    private val folderLayoutItemBinding: FolderLayoutItemBinding,
    private val dataToTransferRecyclerViewItemClickListener: DataToTransferRecyclerViewItemClickListener<DataToTransfer>,
    private val folderClickedListener: DataToTransferRecyclerViewItemClickListener<String>
) : RecyclerView.ViewHolder(folderLayoutItemBinding.root) {

    fun bindData(
        dataToTransfer: DataToTransfer,
        selectedFilesToTransfer: MutableList<DataToTransfer>
    ) {

        dataToTransfer as DataToTransfer.DeviceFile
        folderLayoutItemBinding.run {
            folderName = dataToTransfer.dataDisplayName

            isItemSelected(
                folderLayoutItemViewGroup,
                folderLayoutItemFolderSelectedCheckBox,
                selectedFilesToTransfer.contains(dataToTransfer)
            )


            folderLayoutItemViewGroup.setOnClickListener {
                folderClickedListener.onClick(dataToTransfer.file.path)
            }
            folderLayoutItemFolderSelectedCheckBox.setOnClickListener {
                isItemSelected(
                    folderLayoutItemViewGroup,
                    folderLayoutItemFolderSelectedCheckBox,
                    !selectedFilesToTransfer.contains(dataToTransfer)
                )
                dataToTransfer.dataSize = dataToTransfer.file.getDirectorySize()
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