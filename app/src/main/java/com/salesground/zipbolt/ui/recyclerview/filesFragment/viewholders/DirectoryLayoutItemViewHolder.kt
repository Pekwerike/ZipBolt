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
import com.salesground.zipbolt.ui.recyclerview.RecyclerViewItemClickedListener
import com.salesground.zipbolt.utils.getDirectorySize


class DirectoryLayoutItemViewHolder(
    private val folderLayoutItemBinding: FolderLayoutItemBinding,
    private val directoryLayoutClickedListener: RecyclerViewItemClickedListener<DataToTransfer>,
    private val folderClickedListener: RecyclerViewItemClickedListener<String>
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
                directoryLayoutClickedListener.onClick(dataToTransfer)
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
            directoryLayoutClickedListener: RecyclerViewItemClickedListener<DataToTransfer>,
            folderClickedListener: RecyclerViewItemClickedListener<String>
        ): DirectoryLayoutItemViewHolder {
            val layoutItemBinding = DataBindingUtil.inflate<FolderLayoutItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.folder_layout_item,
                parent,
                false
            )
            return DirectoryLayoutItemViewHolder(
                layoutItemBinding,
                directoryLayoutClickedListener,
                folderClickedListener
            )
        }
    }
}