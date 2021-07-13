package com.salesground.zipbolt.ui.recyclerview.directoryListDisplayFragment

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.model.DocumentType
import com.salesground.zipbolt.ui.recyclerview.DataToTransferDiffUtill
import com.salesground.zipbolt.ui.recyclerview.DataToTransferRecyclerViewItemClickListener

class DirectoryListDisplayRecyclerViewAdapter(
    private val context: Context,
    private val dataToTransferRecyclerViewItemClickListener: DataToTransferRecyclerViewItemClickListener
) : ListAdapter<DataToTransfer,
        RecyclerView.ViewHolder>(DataToTransferDiffUtill()) {

    enum class DirectoryListMediaItemType(val value: Int) {
        DIRECTORY(1),
        IMAGE(2),
        VIDEO(3),
        AUDIO(4),
        APP(5),
        DOCUMENT(6)
    }

    override fun getItemViewType(position: Int): Int {
        return when (val documentType = getItem(position).getFileType(context)) {
            DocumentType.App -> documentType.value
            DocumentType.Audio -> documentType.value
            DocumentType.Directory -> documentType.value
            DocumentType.Document.ExcelFile -> documentType.value
            DocumentType.Document.Pdf -> documentType.value
            DocumentType.Document.UnknownDocument -> documentType.value
            DocumentType.Document.WordDocument -> documentType.value
            DocumentType.Image -> documentType.value
            DocumentType.Video -> documentType.value
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            DocumentType.Directory.value -> {
                DirectoryLayoutItemViewHolder.createViewHolder(
                    parent,
                    dataToTransferRecyclerViewItemClickListener
                )
            }
            else -> {
                DirectoryLayoutItemViewHolder.createViewHolder(
                    parent,
                    dataToTransferRecyclerViewItemClickListener
                )
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DirectoryLayoutItemViewHolder -> holder.bindData(getItem(position))
        }
    }
}