package com.salesground.zipbolt.ui.recyclerview.sentDataFragment.viewHolders.plainFile.video

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.salesground.zipbolt.R
import com.salesground.zipbolt.databinding.PlainVideoFileTransferLayoutItemBinding
import com.salesground.zipbolt.model.DataToTransfer

class PlainVideoFileTransferCompleteLayoutItemViewHolder(
    private val plainVideoFileTransferLayoutItemBinding: PlainVideoFileTransferLayoutItemBinding
) : RecyclerView.ViewHolder(plainVideoFileTransferLayoutItemBinding.root) {

    fun bindData(dataToTransfer: DataToTransfer) {
        plainVideoFileTransferLayoutItemBinding.run {
            this.document = dataToTransfer as DataToTransfer.DeviceFile
            plainVideoFileTransferLayoutItemShimmer.run {
                stopShimmer()
                hideShimmer()
            }
        }
    }

    companion object {
        fun createViewHolder(parent: ViewGroup): PlainVideoFileTransferCompleteLayoutItemViewHolder {
            val layoutBinding = DataBindingUtil.inflate<PlainVideoFileTransferLayoutItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.plain_video_file_transfer_layout_item,
                parent,
                false
            )
            return PlainVideoFileTransferCompleteLayoutItemViewHolder(layoutBinding)
        }
    }
}