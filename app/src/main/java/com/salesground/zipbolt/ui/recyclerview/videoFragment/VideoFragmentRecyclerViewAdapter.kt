package com.salesground.zipbolt.ui.recyclerview.videoFragment

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.ui.recyclerview.DataToTransferDiffUtill
import com.salesground.zipbolt.ui.recyclerview.DataToTransferRecyclerViewItemClickListener

class VideoFragmentRecyclerViewAdapter(
    private val dataToTransferRecyclerViewItemClickListener:
    DataToTransferRecyclerViewItemClickListener,
    private val selectedVideos: MutableList<DataToTransfer>
) : ListAdapter<DataToTransfer, RecyclerView.ViewHolder>(DataToTransferDiffUtill()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return VideoLayoutItemViewHolder.createViewHolder(
            parent,
            dataToTransferRecyclerViewItemClickListener
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is VideoLayoutItemViewHolder) {
            holder.bindVideoData(getItem(position), selectedVideos)
        }
    }
}