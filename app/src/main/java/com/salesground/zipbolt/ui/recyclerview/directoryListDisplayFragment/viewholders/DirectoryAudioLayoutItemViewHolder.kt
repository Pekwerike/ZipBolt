package com.salesground.zipbolt.ui.recyclerview.directoryListDisplayFragment.viewholders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.salesground.zipbolt.R
import com.salesground.zipbolt.databinding.FolderAudioLayoutItemBinding
import com.salesground.zipbolt.model.DataToTransfer

class DirectoryAudioLayoutItemViewHolder(
    private val folderAudioLayoutItemBinding: FolderAudioLayoutItemBinding
) : RecyclerView.ViewHolder(folderAudioLayoutItemBinding.root) {

    fun bindData(dataToTransfer: DataToTransfer) {
        dataToTransfer as DataToTransfer.DeviceFile
        folderAudioLayoutItemBinding.run {
            audioFile = dataToTransfer.file
            folderAudioLayoutItemLayoutViewGroup.setOnClickListener {

            }
            folderAudioLayoutItemFolderSelectedCheckBox.setOnClickListener {

            }
        }
    }

    companion object {
        fun createViewHolder(parent: ViewGroup): DirectoryAudioLayoutItemViewHolder {
            val layoutItemBinding = DataBindingUtil.inflate<FolderAudioLayoutItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.folder_audio_layout_item,
                parent,
                false
            )
            return DirectoryAudioLayoutItemViewHolder(layoutItemBinding)
        }
    }
}