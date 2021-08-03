package com.salesground.zipbolt.ui.recyclerview.ongoingDataTransferRecyclerViewComponents.viewHolders.video

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.salesground.zipbolt.R
import com.salesground.zipbolt.databinding.VideoTransferLayoutItemBinding
import com.salesground.zipbolt.model.DataToTransfer

class VideoReceiveCompleteLayoutItemViewHolder(
    private val videoTransferLayoutItemBinding: VideoTransferLayoutItemBinding
) : RecyclerView.ViewHolder(videoTransferLayoutItemBinding.root) {

    fun bindData(dataToTransfer: DataToTransfer) {
        dataToTransfer as DataToTransfer.DeviceVideo
        videoTransferLayoutItemBinding.run {
            videoSize = dataToTransfer.videoSize
            videoDuration = dataToTransfer.videoDuration
            videoName = dataToTransfer.videoDisplayName

            videoTransferLayoutItemShimmer.run {
                stopShimmer()
                hideShimmer()
            }

            Glide.with(videoTransferLayoutItemVideoPreviewImageView)
                .load(dataToTransfer.dataUri)
                .into(videoTransferLayoutItemVideoPreviewImageView)
        }
    }

    companion object {
        fun createViewHolder(parent: ViewGroup): VideoReceiveCompleteLayoutItemViewHolder {
            val layoutBinding = DataBindingUtil.inflate<VideoTransferLayoutItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.video_transfer_layout_item,
                parent,
                false
            )
            return VideoReceiveCompleteLayoutItemViewHolder(layoutBinding)
        }
    }
}