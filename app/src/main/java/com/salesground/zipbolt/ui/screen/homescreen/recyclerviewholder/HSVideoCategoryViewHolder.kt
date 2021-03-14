package com.salesground.zipbolt.ui.screen.homescreen.recyclerviewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.salesground.zipbolt.R
import com.salesground.zipbolt.databinding.VideoLayoutItemBinding
import com.salesground.zipbolt.ui.screen.homescreen.recyclerviewadapter.datamodel.DataCategory

class HSVideoCategoryViewHolder(private val videoLayoutItemBinding: VideoLayoutItemBinding) :
    RecyclerView.ViewHolder(videoLayoutItemBinding.root) {

    fun bindVideoData(videoData : DataCategory.Video){
        videoLayoutItemBinding.apply {
            Glide.with(root.context)
                .load(videoData.videoModel.mediaUri)
                .into(deviceVideoPreviewImageView)
        }
    }

    companion object {
        fun createHSVideoCategoryViewHolder(parent: ViewGroup): HSVideoCategoryViewHolder {
            val layoutItemBinding = DataBindingUtil.inflate<VideoLayoutItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.video_layout_item,
                parent,
                false
            )
            return HSVideoCategoryViewHolder(layoutItemBinding)
        }
    }
}