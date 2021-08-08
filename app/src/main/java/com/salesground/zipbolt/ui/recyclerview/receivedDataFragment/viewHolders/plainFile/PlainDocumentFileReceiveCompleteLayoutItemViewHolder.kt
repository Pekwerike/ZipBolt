package com.salesground.zipbolt.ui.recyclerview.receivedDataFragment.viewHolders.plainFile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.salesground.zipbolt.R
import com.salesground.zipbolt.databinding.PlainDocumentFileTransferLayoutItemBinding
import com.salesground.zipbolt.model.DataToTransfer

class PlainDocumentFileReceiveCompleteLayoutItemViewHolder(
    private val plainDocumentFileTransferLayoutItemBinding: PlainDocumentFileTransferLayoutItemBinding
) : RecyclerView.ViewHolder(plainDocumentFileTransferLayoutItemBinding.root) {

    fun bindData(dataToTransfer: DataToTransfer) {
        plainDocumentFileTransferLayoutItemBinding.run {
            document = dataToTransfer as DataToTransfer.DeviceFile
            plainDocumentFileTransferLayoutItemShimmer.run {
                stopShimmer()
                hideShimmer()
            }
        }
    }

    companion object {
        fun createViewHolder(parent: ViewGroup): PlainDocumentFileReceiveCompleteLayoutItemViewHolder {
            val layoutBinding = DataBindingUtil.inflate<PlainDocumentFileTransferLayoutItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.plain_document_file_transfer_layout_item,
                parent,
                false
            )
            return PlainDocumentFileReceiveCompleteLayoutItemViewHolder(layoutBinding)
        }
    }
}