package com.salesground.zipbolt.ui.recyclerview.sentDataFragment.viewHolders.plainFile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.salesground.zipbolt.R
import com.salesground.zipbolt.databinding.PlainFileTransferLayoutItemBinding
import com.salesground.zipbolt.model.DataToTransfer

class PlainFileTransferWaitingLayoutItemViewHolder(
    private val plainFileTransferLayoutItemBinding: PlainFileTransferLayoutItemBinding
) : RecyclerView.ViewHolder(plainFileTransferLayoutItemBinding.root) {

    fun bindData(dataToTransfer: DataToTransfer) {
        plainFileTransferLayoutItemBinding.run {
            document = dataToTransfer as DataToTransfer.DeviceFile
        }
    }

    companion object {
        fun createViewHolder(parent: ViewGroup): PlainFileTransferWaitingLayoutItemViewHolder {
            val layoutBinding = DataBindingUtil.inflate<PlainFileTransferLayoutItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.plain_file_transfer_layout_item,
                parent,
                false
            )
            return PlainFileTransferWaitingLayoutItemViewHolder(layoutBinding)
        }
    }
}