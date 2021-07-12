package com.salesground.zipbolt.ui.recyclerview.directoryListDisplayFragment

import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.ui.recyclerview.DataToTransferDiffUtill

class DirectoryListDisplayRecyclerViewAdapter : ListAdapter<DataToTransfer,
        RecyclerView.ViewHolder>(DataToTransferDiffUtill()) {

}