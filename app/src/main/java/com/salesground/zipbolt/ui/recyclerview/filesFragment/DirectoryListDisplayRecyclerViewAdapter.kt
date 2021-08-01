package com.salesground.zipbolt.ui.recyclerview.filesFragment

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.model.MediaType
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
        val dataItem = getItem(position) as DataToTransfer.DeviceFile
        return dataItem.getFileType(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            MediaType.File.Directory.value -> {
                DirectoryLayoutItemViewHolder.createViewHolder(
                    parent,
                    dataToTransferRecyclerViewItemClickListener,
                    folderClickedListener
                )
            }
            MediaType.File.ImageFile.value -> {
                DirectoryImageLayoutItemViewHolder.createViewHolder(
                    parent,
                    dataToTransferRecyclerViewItemClickListener
                )
            }
            MediaType.File.VideoFile.value -> {
                DirectoryVideoLayoutItemViewHolder.createViewHolder(parent)
            }
            MediaType.File.AudioFile.value -> {
                DirectoryAudioLayoutItemViewHolder.createViewHolder(parent)
            }
            MediaType.File.Document.PowerPointDocument.value -> {
                DirectoryDocumentLayoutItemViewHolder.createViewHolder(parent)
            }
            MediaType.File.Document.WordDocument.value -> {
                DirectoryDocumentLayoutItemViewHolder.createViewHolder(parent)
            }
            MediaType.File.Document.PdfDocument.value -> {
                DirectoryDocumentLayoutItemViewHolder.createViewHolder(parent)
            }
            MediaType.File.Document.ExcelDocument.value -> {
                DirectoryDocumentLayoutItemViewHolder.createViewHolder(parent)
            }
            MediaType.File.Document.ZipDocument.value -> {
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