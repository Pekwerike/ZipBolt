package com.salesground.zipbolt.ui.recyclerview.filesFragment

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.model.MediaType
import com.salesground.zipbolt.ui.recyclerview.DataToTransferRecyclerViewDiffUtil
import com.salesground.zipbolt.ui.recyclerview.RecyclerViewItemClickedListener
import com.salesground.zipbolt.ui.recyclerview.filesFragment.viewholders.*

class DirectoryListDisplayRecyclerViewAdapter(
    private val directoryListDisplayLayoutClickedListener: RecyclerViewItemClickedListener<DataToTransfer>,
    private val folderClickedListener: RecyclerViewItemClickedListener<String>,
    var filesSelectedForTransfer: MutableList<DataToTransfer>
) : ListAdapter<DataToTransfer,
        RecyclerView.ViewHolder>(DataToTransferRecyclerViewDiffUtil()) {


    override fun getItemViewType(position: Int): Int {
        val dataItem = getItem(position) as DataToTransfer.DeviceFile
        return dataItem.dataType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            MediaType.File.Directory.value -> {
                DirectoryLayoutItemViewHolder.createViewHolder(
                    parent,
                    directoryListDisplayLayoutClickedListener,
                    folderClickedListener
                )
            }
            MediaType.File.AppFile.value -> {
                DirectoryAppLayoutItemViewHolder.createViewHolder(
                    parent,
                    directoryListDisplayLayoutClickedListener
                )
            }
            MediaType.File.ImageFile.value -> {
                DirectoryImageLayoutItemViewHolder.createViewHolder(
                    parent,
                    directoryListDisplayLayoutClickedListener
                )
            }
            MediaType.File.VideoFile.value -> {
                DirectoryVideoLayoutItemViewHolder.createViewHolder(
                    parent,
                    directoryListDisplayLayoutClickedListener
                )
            }
            MediaType.File.AudioFile.value -> {
                DirectoryAudioLayoutItemViewHolder.createViewHolder(
                    parent,
                    directoryListDisplayLayoutClickedListener
                )
            }
            MediaType.File.Document.PowerPointDocument.value -> {
                DirectoryDocumentLayoutItemViewHolder.createViewHolder(
                    parent,
                    directoryListDisplayLayoutClickedListener
                )
            }
            MediaType.File.Document.WordDocument.value -> {
                DirectoryDocumentLayoutItemViewHolder.createViewHolder(
                    parent,
                    directoryListDisplayLayoutClickedListener
                )
            }
            MediaType.File.Document.PdfDocument.value -> {
                DirectoryDocumentLayoutItemViewHolder.createViewHolder(
                    parent,
                    directoryListDisplayLayoutClickedListener
                )
            }
            MediaType.File.Document.ExcelDocument.value -> {
                DirectoryDocumentLayoutItemViewHolder.createViewHolder(
                    parent,
                    directoryListDisplayLayoutClickedListener
                )
            }
            MediaType.File.Document.ZipDocument.value -> {
                DirectoryDocumentLayoutItemViewHolder.createViewHolder(
                    parent,
                    directoryListDisplayLayoutClickedListener
                )
            }
            else -> {
                DirectoryDocumentLayoutItemViewHolder.createViewHolder(
                    parent,
                    directoryListDisplayLayoutClickedListener
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
            is DirectoryVideoLayoutItemViewHolder -> holder.bindData(
                getItem(position),
                filesSelectedForTransfer
            )
            is DirectoryAudioLayoutItemViewHolder -> holder.bindData(
                getItem(position),
                filesSelectedForTransfer
            )
            is DirectoryDocumentLayoutItemViewHolder -> holder.bindData(
                getItem(position),
                filesSelectedForTransfer
            )
            is DirectoryAppLayoutItemViewHolder -> holder.bindData(
                getItem(position),
                filesSelectedForTransfer
            )
        }
    }
}