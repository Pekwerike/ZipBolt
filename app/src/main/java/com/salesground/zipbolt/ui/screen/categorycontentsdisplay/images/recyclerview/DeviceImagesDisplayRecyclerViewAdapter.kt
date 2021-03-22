package com.salesground.zipbolt.ui.screen.categorycontentsdisplay.images.recyclerview

import android.view.ViewGroup
import androidx.collection.ArrayMap
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.salesground.zipbolt.ui.screen.categorycontentsdisplay.images.dto.ImagesDisplayModel

enum class DeviceImagesDisplayViewHolderType(val type: Int) {
    IMAGE(1),
    GROUP_HEADER(2)
}


class DeviceImagesDisplayRecyclerViewAdapter(
    //TODO pass onclick listener to listen for click events on images
) : ListAdapter<ImagesDisplayModel, RecyclerView.ViewHolder>(DeviceImagesDisplayRecyclerViewDiffUtil) {

    private val collectionOfClickedImages: ArrayMap<ImagesDisplayModel, Boolean> = ArrayMap()

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ImagesDisplayModel.DeviceImageDisplay -> DeviceImagesDisplayViewHolderType.IMAGE.type
            is ImagesDisplayModel.ImagesDateModifiedHeader -> DeviceImagesDisplayViewHolderType.GROUP_HEADER.type
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            DeviceImagesDisplayViewHolderType.IMAGE.type -> DeviceImageViewHolder.createDeviceImageViewHolder(
                parent
            )
            DeviceImagesDisplayViewHolderType.GROUP_HEADER.type -> DateModifiedHeaderViewHolder.createDateModifiedHeaderViewHolder(
                parent
            )
            else -> DeviceImageViewHolder.createDeviceImageViewHolder(
                parent
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DeviceImageViewHolder -> {
                val imageForViewHolder = getItem(position) as ImagesDisplayModel.DeviceImageDisplay
                holder.bindImageDisplay(
                    data = imageForViewHolder,
                    onClick = {
                        if(collectionOfClickedImages.containsKey(it)){
                            // un clicked
                            collectionOfClickedImages.remove(it)
                        }else{
                            // clicked
                            collectionOfClickedImages[it] = true
                        }
                    },
                  clickedImages = collectionOfClickedImages)

            }
            is DateModifiedHeaderViewHolder -> holder.bindDateModified(getItem(position) as ImagesDisplayModel.ImagesDateModifiedHeader)
        }
    }
}

private object DeviceImagesDisplayRecyclerViewDiffUtil :
    DiffUtil.ItemCallback<ImagesDisplayModel>() {
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