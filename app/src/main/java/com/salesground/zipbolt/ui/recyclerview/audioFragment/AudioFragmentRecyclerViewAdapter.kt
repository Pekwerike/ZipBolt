package com.salesground.zipbolt.ui.recyclerview.audioFragment

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.ui.recyclerview.DataToTransferDiffUtill
import com.salesground.zipbolt.ui.recyclerview.DataToTransferRecyclerViewItemClickListener

class AudioFragmentRecyclerViewAdapter(
    private val dataToTransferRecyclerViewItemClickListener:
    DataToTransferRecyclerViewItemClickListener<DataToTransfer>,
    private val selectedAudios: MutableList<DataToTransfer>
) : ListAdapter<DataToTransfer, RecyclerView.ViewHolder>(DataToTransferDiffUtill()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AudioLayoutItemViewHolder.createViewHolder(
            parent,
            dataToTransferRecyclerViewItemClickListener
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is AudioLayoutItemViewHolder) {
            holder.bindData(getItem(position), selectedAudios)
        }
    }


}