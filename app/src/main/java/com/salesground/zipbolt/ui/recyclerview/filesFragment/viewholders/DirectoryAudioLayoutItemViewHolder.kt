package com.salesground.zipbolt.ui.recyclerview.filesFragment.viewholders

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.salesground.zipbolt.R
import com.salesground.zipbolt.databinding.FolderAudioLayoutItemBinding
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.ui.customviews.SelectableLinearLayout
import com.salesground.zipbolt.ui.recyclerview.DataToTransferRecyclerViewItemClickListener

class DirectoryAudioLayoutItemViewHolder(
    private val folderAudioLayoutItemBinding: FolderAudioLayoutItemBinding,
    private val dataToTransferRecyclerViewItemClickListener: DataToTransferRecyclerViewItemClickListener<DataToTransfer>
) : RecyclerView.ViewHolder(folderAudioLayoutItemBinding.root) {

    fun bindData(
        dataToTransfer: DataToTransfer,
        filesSelectedForTransfer: MutableList<DataToTransfer>
    ) {
        dataToTransfer as DataToTransfer.DeviceFile

        folderAudioLayoutItemBinding.run {
            audioFile = dataToTransfer.file
            if (filesSelectedForTransfer.contains(dataToTransfer)) {
                folderAudioLayoutItemFolderSelectedCheckBox.isChecked = true
                folderAudioLayoutItemLayoutViewGroup.setIsViewSelected(true)
            } else {
                folderAudioLayoutItemFolderSelectedCheckBox.isChecked = true
                folderAudioLayoutItemLayoutViewGroup.setIsViewSelected(true)
            }


            folderAudioLayoutItemLayoutViewGroup.setOnClickListener {
                isItemSelected(
                    folderAudioLayoutItemLayoutViewGroup,
                    folderAudioLayoutItemFolderSelectedCheckBox,
                    !filesSelectedForTransfer.contains(dataToTransfer)
                )
                dataToTransferRecyclerViewItemClickListener.onClick(dataToTransfer)

            }
            folderAudioLayoutItemFolderSelectedCheckBox.setOnClickListener {
                isItemSelected(
                    folderAudioLayoutItemLayoutViewGroup,
                    folderAudioLayoutItemFolderSelectedCheckBox,
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
        ): DirectoryAudioLayoutItemViewHolder {
            val layoutItemBinding = DataBindingUtil.inflate<FolderAudioLayoutItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.folder_audio_layout_item,
                parent,
                false
            )
            return DirectoryAudioLayoutItemViewHolder(
                layoutItemBinding,
                dataToTransferRecyclerViewItemClickListener
            )
        }
    }
}