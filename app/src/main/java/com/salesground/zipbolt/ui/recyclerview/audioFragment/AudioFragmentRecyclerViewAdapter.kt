package com.salesground.zipbolt.ui.recyclerview.audioFragment

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.ui.recyclerview.DataToTransferRecyclerViewDiffUtil
import com.salesground.zipbolt.ui.recyclerview.RecyclerViewItemClickedListener

class AudioFragmentRecyclerViewAdapter(
    private val audioLayoutClickedListener:
    RecyclerViewItemClickedListener<DataToTransfer>,
    var selectedAudios: MutableList<DataToTransfer>
) : ListAdapter<DataToTransfer, RecyclerView.ViewHolder>(DataToTransferRecyclerViewDiffUtil()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AudioLayoutItemViewHolder.createViewHolder(
            parent,
            audioLayoutClickedListener
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is AudioLayoutItemViewHolder) {
            holder.bindData(getItem(position), selectedAudios)
        }
    }


}