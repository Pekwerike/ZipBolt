package com.salesground.zipbolt.ui.recyclerview.filesFragment.viewholders

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.salesground.zipbolt.R
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.ui.customviews.SelectableLinearLayout
import com.salesground.zipbolt.ui.recyclerview.RecyclerViewItemClickedListener

class DirectoryImageLayoutItemViewHolder(
    private val folderImageLayoutItemBinding: com.salesground.zipbolt.databinding.FolderImageLayoutItemBinding,
    private val directoryImageLayoutClickedListener: RecyclerViewItemClickedListener<DataToTransfer>
) : RecyclerView.ViewHolder(folderImageLayoutItemBinding.root) {

    fun bindData(
        dataToTransfer: DataToTransfer,
        filesSelectedForTransfer: MutableList<DataToTransfer>
    ) {
        dataToTransfer as DataToTransfer.DeviceFile
        folderImageLayoutItemBinding.run {
            imageFile = dataToTransfer.file

            Glide.with(root.context)
                .load(dataToTransfer.file)
                .into(folderImageLayoutItemImageView)

            isItemSelected(
                folderImageLayoutItemLayoutViewGroup,
                folderImageLayoutItemFolderSelectedCheckBox,
                filesSelectedForTransfer.contains(dataToTransfer)
            )

            folderImageLayoutItemLayoutViewGroup.setOnClickListener {
                isItemSelected(
                    folderImageLayoutItemLayoutViewGroup,
                    folderImageLayoutItemFolderSelectedCheckBox,
                    !filesSelectedForTransfer.contains(dataToTransfer)
                )
                directoryImageLayoutClickedListener.onClick(dataToTransfer)
            }

            folderImageLayoutItemFolderSelectedCheckBox.setOnClickListener {
                isItemSelected(
                    folderImageLayoutItemLayoutViewGroup,
                    folderImageLayoutItemFolderSelectedCheckBox,
                    !filesSelectedForTransfer.contains(dataToTransfer)
                )
                directoryImageLayoutClickedListener.onClick(dataToTransfer)
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
            directoryImageLayoutClickedListener: RecyclerViewItemClickedListener<DataToTransfer>
        ): DirectoryImageLayoutItemViewHolder {
            val layoutBinding =
                DataBindingUtil.inflate<com.salesground.zipbolt.databinding.FolderImageLayoutItemBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.folder_image_layout_item,
                    parent,
                    false
                )

            return DirectoryImageLayoutItemViewHolder(
                layoutBinding,
                directoryImageLayoutClickedListener
            )
        }
    }
}