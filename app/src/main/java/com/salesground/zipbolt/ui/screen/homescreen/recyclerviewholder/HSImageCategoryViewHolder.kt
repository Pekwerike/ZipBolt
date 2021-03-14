package com.salesground.zipbolt.ui.screen.homescreen.recyclerviewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.salesground.zipbolt.R
import com.salesground.zipbolt.databinding.ImageLayoutItemBinding
import com.salesground.zipbolt.ui.screen.homescreen.recyclerviewadapter.datamodel.DataCategory

class HSImageCategoryViewHolder(
    private val imageLayoutItemBinding: ImageLayoutItemBinding
) : RecyclerView.ViewHolder(imageLayoutItemBinding.root) {

    fun bindImageDisplay(data : DataCategory.Image){
        imageLayoutItemBinding.apply{
            Glide.with(root.context)
                .load(data.imageModel.mediaUri)
                .into(deviceImageDisplayImageView)
        }
    }

    companion object{
        fun createHSImageCategoryViewHolder(parent: ViewGroup) : HSImageCategoryViewHolder{
            val layoutItem = DataBindingUtil.inflate<ImageLayoutItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.image_layout_item,
                parent,
                false
            )
            return HSImageCategoryViewHolder(layoutItem)
        }
    }

}