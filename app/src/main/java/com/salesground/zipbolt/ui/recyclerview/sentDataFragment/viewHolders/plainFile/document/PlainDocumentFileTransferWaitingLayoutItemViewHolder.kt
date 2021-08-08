package com.salesground.zipbolt.ui.recyclerview.sentDataFragment.viewHolders.plainFile.document

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.salesground.zipbolt.R
import com.salesground.zipbolt.databinding.PlainDocumentFileTransferLayoutItemBinding
import com.salesground.zipbolt.model.DataToTransfer

class PlainDocumentFileTransferWaitingLayoutItemViewHolder(
    private val plainDocumentFileTransferLayoutItemBinding: PlainDocumentFileTransferLayoutItemBinding
): RecyclerView.ViewHolder(plainDocumentFileTransferLayoutItemBinding.root) {

    fun bindData(dataToTransfer: DataToTransfer){
        plainDocumentFileTransferLayoutItemBinding.run {
            document = dataToTransfer as DataToTransfer.DeviceFile
        }
    }

    companion object{
        fun createViewHolder(parent: ViewGroup): PlainDocumentFileTransferWaitingLayoutItemViewHolder{
            val layoutBinding = DataBindingUtil.inflate<PlainDocumentFileTransferLayoutItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.plain_document_file_transfer_layout_item,
                parent,
                false
            )
            return PlainDocumentFileTransferWaitingLayoutItemViewHolder(layoutBinding)
        }
    }
}