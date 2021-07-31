package com.salesground.zipbolt.ui.recyclerview.filesFragment

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.model.DocumentType
import com.salesground.zipbolt.ui.recyclerview.DataToTransferRecyclerViewDiffUtil
import com.salesground.zipbolt.ui.recyclerview.DataToTransferRecyclerViewItemClickListener
import com.salesground.zipbolt.ui.recyclerview.filesFragment.viewholders.*

class DirectoryListDisplayRecyclerViewAdapter(
    private val context: Context,
    private val dataToTransferRecyclerViewItemClickListener: DataToTransferRecyclerViewItemClickListener<DataToTransfer>,
    private val folderClickedListener: DataToTransferRecyclerViewItemClickListener<String>,
    private val filesSelectedForTransfer: MutableList<DataToTransfer>
) : ListAdapter<DataToTransfer,
        RecyclerView.ViewHolder>(DataToTransferRecyclerViewDiffUtil()) {


    override fun getItemViewType(position: Int): Int {
        return when (val documentType = getItem(position).getFileType(context)) {
            DocumentType.App -> documentType.value
            DocumentType.Audio -> documentType.value
            DocumentType.Directory -> documentType.value
            DocumentType.Document.ExcelDocument -> documentType.value
            DocumentType.Document.Pdf -> documentType.value
            DocumentType.Document.UnknownDocument -> documentType.value
            DocumentType.Document.WordDocument -> documentType.value
            DocumentType.Document.ZipDocument -> documentType.value
            DocumentType.Image -> documentType.value
            DocumentType.Video -> documentType.value
            else -> documentType.value
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            DocumentType.Directory.value -> {
                DirectoryLayoutItemViewHolder.createViewHolder(
                    parent,
                    dataToTransferRecyclerViewItemClickListener,
                    folderClickedListener
                )
            }
            DocumentType.Image.value -> {
                DirectoryImageLayoutItemViewHolder.createViewHolder(
                    parent,
                    dataToTransferRecyclerViewItemClickListener
                )
            }
            DocumentType.Video.value -> {
                DirectoryVideoLayoutItemViewHolder.createViewHolder(parent)
            }
            DocumentType.Audio.value -> {
                DirectoryAudioLayoutItemViewHolder.createViewHolder(parent)
            }
            DocumentType.Document.PowerPointDocument.value -> {
                DirectoryDocumentLayoutItemViewHolder.createViewHolder(parent)
            }
            DocumentType.Document.WordDocument.value -> {
                DirectoryDocumentLayoutItemViewHolder.createViewHolder(parent)
            }
            DocumentType.Document.Pdf.value -> {
                DirectoryDocumentLayoutItemViewHolder.createViewHolder(parent)
            }
            DocumentType.Document.ExcelDocument.value -> {
                DirectoryDocumentLayoutItemViewHolder.createViewHolder(parent)
            }
            DocumentType.Document.ZipDocument.value -> {
                DirectoryDocumentLayoutItemViewHolder.createViewHolder(parent)
            }
            else -> {
                DirectoryLayoutItemViewHolder.createViewHolder(
                    parent,
                    dataToTransferRecyclerViewItemClickListener,
                    folderClickedListener
                )
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DirectoryLayoutItemViewHolder -> holder.bindData(
                getItem(position),
                filesSelectedForTransfer
            )
            is DirectoryImageLayoutItemViewHolder -> holder.bindData(
                getItem(position),
                filesSelectedForTransfer
            )
            is DirectoryVideoLayoutItemViewHolder -> holder.bindData(getItem(position))
            is DirectoryAudioLayoutItemViewHolder -> holder.bindData(getItem(position))
            is DirectoryDocumentLayoutItemViewHolder -> holder.bindData(getItem(position))
        }
    }
}