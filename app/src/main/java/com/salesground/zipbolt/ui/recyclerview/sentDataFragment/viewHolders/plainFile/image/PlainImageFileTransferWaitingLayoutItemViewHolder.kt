package com.salesground.zipbolt.ui.recyclerview.sentDataFragment.viewHolders.plainFile.image

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.salesground.zipbolt.R
import com.salesground.zipbolt.databinding.PlainImageFileTransferLayoutItemBinding
import com.salesground.zipbolt.model.DataToTransfer

class PlainImageFileTransferWaitingLayoutItemViewHolder(
    private val plainImageFileTransferLayoutItemBinding: PlainImageFileTransferLayoutItemBinding
) : RecyclerView.ViewHolder(plainImageFileTransferLayoutItemBinding.root) {

    fun bindData(dataToTransfer: DataToTransfer) {
        plainImageFileTransferLayoutItemBinding.run {
            document = dataToTransfer as DataToTransfer.DeviceFile
        }
    }

    companion object {
        fun createViewHolder(parent: ViewGroup): PlainImageFileTransferWaitingLayoutItemViewHolder {
            val layoutBinding = DataBindingUtil.inflate<PlainImageFileTransferLayoutItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.plain_image_file_transfer_layout_item,
                parent,
                false
            )
            return PlainImageFileTransferWaitingLayoutItemViewHolder(layoutBinding)
        }
    }
}