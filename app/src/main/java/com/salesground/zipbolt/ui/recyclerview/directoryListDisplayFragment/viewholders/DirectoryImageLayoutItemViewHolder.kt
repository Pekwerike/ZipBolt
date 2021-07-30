package com.salesground.zipbolt.ui.recyclerview.directoryListDisplayFragment.viewholders

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.salesground.zipbolt.R
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.ui.customviews.SelectableLinearLayout
import com.salesground.zipbolt.ui.recyclerview.DataToTransferRecyclerViewItemClickListener
import java.io.File

class DirectoryImageLayoutItemViewHolder(
    private val folderImageLayoutItemBinding: com.salesground.zipbolt.databinding.FolderImageLayoutItemBinding,
    private val dataToTransferRecyclerViewItemClickListener: DataToTransferRecyclerViewItemClickListener<DataToTransfer>
) : RecyclerView.ViewHolder(folderImageLayoutItemBinding.root) {

    fun bindData(
        dataToTransfer: DataToTransfer,
        filesSelectedForTransfer: MutableList<DataToTransfer>
    ) {
        dataToTransfer as DataToTransfer.DeviceFile
        folderImageLayoutItemBinding.apply {
            imageFile = dataToTransfer.file

            Glide.with(root.context)
                .load(dataToTransfer.file)
                .into(folderImageLayoutItemImageView)

            if (filesSelectedForTransfer.contains(dataToTransfer)) {
                folderImageLayoutItemLayoutViewGroup.setIsViewSelected(true)
            }

            folderImageLayoutItemLayoutViewGroup.setOnClickListener {
                isItemSelected(
                    folderImageLayoutItemLayoutViewGroup,
                    dataToTransfer,
                    filesSelectedForTransfer,
                    folderImageLayoutItemFolderSelectedCheckBox
                )
                dataToTransferRecyclerViewItemClickListener.onClick(dataToTransfer)
            }

            folderImageLayoutItemFolderSelectedCheckBox.setOnClickListener {
                isItemSelected(
                    folderImageLayoutItemLayoutViewGroup,
                    dataToTransfer,
                    filesSelectedForTransfer,
                    folderImageLayoutItemFolderSelectedCheckBox
                )
                dataToTransferRecyclerViewItemClickListener.onClick(dataToTransfer)
            }
            executePendingBindings()
        }
    }

    private fun isItemSelected(
        viewGroup: SelectableLinearLayout,
        selectedData: DataToTransfer,
        filesSelectedForTransfer: MutableList<DataToTransfer>,
        selectedCheckBox: CheckBox
    ) {
        if (filesSelectedForTransfer.contains(selectedData)) {
            viewGroup.setIsViewSelected(false)
            selectedCheckBox.isSelected = false
            filesSelectedForTransfer.remove(selectedData)
        } else {
            selectedCheckBox.isSelected = true
            viewGroup.setIsViewSelected(true)
            filesSelectedForTransfer.add(selectedData)
        }
    }


    companion object {
        fun createViewHolder(
            parent: ViewGroup,
            dataToTransferRecyclerViewItemClickListener: DataToTransferRecyclerViewItemClickListener<DataToTransfer>
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
                dataToTransferRecyclerViewItemClickListener
            )
        }
    }
}