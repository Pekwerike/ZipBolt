package com.salesground.zipbolt.ui.screen.categorycontentsdisplay.images.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.salesground.zipbolt.R
import com.salesground.zipbolt.databinding.ImageLayoutItemBinding
import com.salesground.zipbolt.ui.screen.categorycontentsdisplay.images.ImagesDisplayModel
import com.salesground.zipbolt.ui.screen.homescreen.recyclerviewadapter.datamodel.DataCategory

class DeviceImageViewHolder(
    private val imageLayoutItemBinding:
    ImageLayoutItemBinding
) : RecyclerView.ViewHolder(imageLayoutItemBinding.root) {

    fun bindImageDisplay(data : ImagesDisplayModel.DeviceImageDisplay){
        imageLayoutItemBinding.apply{
            Glide.with(root.context)
                .load(data.deviceImage.imageUri)
                .into(deviceImageDisplayImageView)
        }
    }

    companion object {
        fun createDeviceImageViewHolder(parent: ViewGroup): DeviceImageViewHolder {
            val layoutItemBinding =
                DataBindingUtil.inflate<ImageLayoutItemBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.image_layout_item,
                    parent,
                    false
                )
            return DeviceImageViewHolder(layoutItemBinding)
        }
    }
}