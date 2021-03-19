package com.salesground.zipbolt.ui.screen.categorycontentsdisplay.images.recyclerview

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.salesground.zipbolt.ui.screen.categorycontentsdisplay.images.ImagesDisplayModel

enum class DeviceImagesDisplayViewHolderType(val type: Int){
    IMAGE(1),
    GROUP_HEADER(2)
}

class DeviceImagesDisplayRecyclerViewAdapter :
    ListAdapter<ImagesDisplayModel, RecyclerView.ViewHolder>(DeviceImagesDisplayRecyclerViewDiffUtil) {

    override fun getItemViewType(position: Int): Int {
        return when(getItem(position)){
            is ImagesDisplayModel.DeviceImageDisplay -> DeviceImagesDisplayViewHolderType.IMAGE.type
            is ImagesDisplayModel.ImagesDateModifiedHeader -> DeviceImagesDisplayViewHolderType.GROUP_HEADER.type
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when(viewType){
          DeviceImagesDisplayViewHolderType.IMAGE.type ->
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }


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