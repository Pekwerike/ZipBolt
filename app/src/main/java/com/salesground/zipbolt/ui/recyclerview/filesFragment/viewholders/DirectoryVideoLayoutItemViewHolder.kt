package com.salesground.zipbolt.ui.recyclerview.filesFragment.viewholders

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.salesground.zipbolt.R
import com.salesground.zipbolt.databinding.FolderVideoLayoutItemBinding
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.ui.customviews.SelectableLinearLayout
import com.salesground.zipbolt.ui.recyclerview.RecyclerViewItemClickedListener

class DirectoryVideoLayoutItemViewHolder(
    private val folderVideoLayoutItemBinding: FolderVideoLayoutItemBinding,
    private val directoryVideoLayoutItemClickedListener: RecyclerViewItemClickedListener<DataToTransfer>
) : RecyclerView.ViewHolder(folderVideoLayoutItemBinding.root) {


    fun bindData(
        dataToTransfer: DataToTransfer,
        filesSelectedForTransfer: MutableList<DataToTransfer>
    ) {
        dataToTransfer as DataToTransfer.DeviceFile
        folderVideoLayoutItemBinding.apply {
            videoFile = dataToTransfer.file

            Glide.with(root.context)
                .load(dataToTransfer.file)
                .into(folderVideoLayoutItemImageView)

            isItemSelected(
                folderVideoLayoutItemLayoutViewGroup,
                folderVideoLayoutItemFolderSelectedCheckBox,
                filesSelectedForTransfer.contains(dataToTransfer)
            )

            folderVideoLayoutItemLayoutViewGroup.setOnClickListener {
                isItemSelected(
                    folderVideoLayoutItemLayoutViewGroup,
                    folderVideoLayoutItemFolderSelectedCheckBox,
                    !filesSelectedForTransfer.contains(dataToTransfer)
                )
                directoryVideoLayoutItemClickedListener.onClick(dataToTransfer)
            }

            folderVideoLayoutItemFolderSelectedCheckBox.setOnClickListener {
                isItemSelected(
                    folderVideoLayoutItemLayoutViewGroup,
                    folderVideoLayoutItemFolderSelectedCheckBox,
                    !filesSelectedForTransfer.contains(dataToTransfer)
                )
                directoryVideoLayoutItemClickedListener.onClick(dataToTransfer)
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
            directoryVideoLayoutClickedListener: RecyclerViewItemClickedListener<DataToTransfer>
        ): DirectoryVideoLayoutItemViewHolder {
            val layoutBinding = DataBindingUtil.inflate<FolderVideoLayoutItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.folder_video_layout_item,
                parent,
                false
            )

            return DirectoryVideoLayoutItemViewHolder(
                layoutBinding,
                directoryVideoLayoutClickedListener
            )
        }
    }
}