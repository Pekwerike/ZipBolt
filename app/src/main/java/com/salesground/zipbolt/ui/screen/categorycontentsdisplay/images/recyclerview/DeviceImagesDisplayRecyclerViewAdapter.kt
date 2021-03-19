package com.salesground.zipbolt.ui.screen.categorycontentsdisplay.images.recyclerview

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.salesground.zipbolt.ui.screen.categorycontentsdisplay.images.ImagesDisplayModel

class DeviceImagesDisplayRecyclerViewAdapter :
    ListAdapter<ImagesDisplayModel, RecyclerView.ViewHolder>(DeviceImagesDisplayRecyclerViewDiffUtil) {


}

object DeviceImagesDisplayRecyclerViewDiffUtil : DiffUtil.ItemCallback<ImagesDisplayModel>(){
    override fun areItemsTheSame(
        oldItem: ImagesDisplayModel,
        newItem: ImagesDisplayModel
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: ImagesDisplayModel,
        newItem: ImagesDisplayModel
    ): Boolean {
       return oldItem == newItem
    }
}