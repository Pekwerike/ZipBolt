package com.salesground.zipbolt.ui.recyclerview.directoryListDisplayFragment

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.ui.recyclerview.DataToTransferDiffUtill

class DirectoryListDisplayRecyclerViewAdapter(
    private val context: Context
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
        val dataToTransfer = getItem(position)
        return when(dataToTransfer.getFileType(context)){
            DataToTransfer.MediaType.IMAGE -> DirectoryListMediaItemType.IMAGE.value
            DataToTransfer.MediaType.VIDEO -> DirectoryListMediaItemType.VIDEO.value
            DataToTransfer.MediaType.AUDIO -> DirectoryListMediaItemType.AUDIO.value
            DataToTransfer.MediaType.APP -> DirectoryListMediaItemType.APP.value
            DataToTransfer.MediaType.FILE -> if(dataToTransfer.da)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

    }
}