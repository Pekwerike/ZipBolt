package com.salesground.zipbolt.ui.recyclerview.videoFragment

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.ui.recyclerview.DataToTransferRecyclerViewDiffUtil
import com.salesground.zipbolt.ui.recyclerview.RecyclerViewItemClickedListener

class VideoFragmentRecyclerViewAdapter(
    private val videoLayoutClickedListener:
    RecyclerViewItemClickedListener<DataToTransfer>,
    var selectedVideos: MutableList<DataToTransfer>
) : ListAdapter<DataToTransfer, RecyclerView.ViewHolder>(DataToTransferRecyclerViewDiffUtil()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return VideoLayoutItemViewHolder.createViewHolder(
            parent,
            videoLayoutClickedListener
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is VideoLayoutItemViewHolder) {
            holder.bindVideoData(getItem(position), selectedVideos)
        }
    }
}