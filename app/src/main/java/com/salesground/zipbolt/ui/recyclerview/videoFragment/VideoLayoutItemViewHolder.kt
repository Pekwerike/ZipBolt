package com.salesground.zipbolt.ui.recyclerview.videoFragment

import android.graphics.drawable.ColorDrawable
import android.media.ThumbnailUtils
import android.os.Build
import android.provider.MediaStore.Images.Thumbnails.MINI_KIND
import android.util.Size
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toFile
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.salesground.zipbolt.R
import com.salesground.zipbolt.databinding.VideoLayoutItemBinding
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.ui.recyclerview.DataToTransferRecyclerViewItemClickListener

class VideoLayoutItemViewHolder(
    private val videoLayoutItemBinding: VideoLayoutItemBinding,
    private val dataToTransferRecyclerViewItemClickListener:
    DataToTransferRecyclerViewItemClickListener<DataToTransfer>
) : RecyclerView.ViewHolder(videoLayoutItemBinding.root) {

    fun bindVideoData(
        dataToTransfer: DataToTransfer,
        selectedVideos: MutableList<DataToTransfer>
    ) {
        dataToTransfer as DataToTransfer.DeviceVideo

        videoLayoutItemBinding.run {
            videoDuration = dataToTransfer.videoDuration
            videoSize = dataToTransfer.videoSize
            videoName = dataToTransfer.videoDisplayName


            Glide.with(videoLayoutItemVideoPreviewImageView)
                .load(dataToTransfer.videoUri)
                .transform(RoundedCorners(30))
                .thumbnail(Glide.with(root.context).load(dataToTransfer.videoUri))
                .into(videoLayoutItemVideoPreviewImageView)

            videoLayoutItemSelectableLinearLayout.run {
                videoLayoutItemVideoSelectedCheckBox.setOnClickListener {
                    dataToTransferRecyclerViewItemClickListener.onClick(
                        dataToTransfer
                    )

                    if (selectedVideos.contains(dataToTransfer)) {
                        // user un-selected, so remove the video from the collection of selected videos
                        setIsViewSelected(true)
                        videoLayoutItemVideoSelectedCheckBox.isChecked = true
                    } else {
                        // user selects, so add the application to the collection of selected videos
                        setIsViewSelected(false)
                        videoLayoutItemVideoSelectedCheckBox.isChecked = false
                    }
                }

                setOnClickListener {
                    dataToTransferRecyclerViewItemClickListener.onClick(
                        dataToTransfer
                    )

                    if (selectedVideos.contains(dataToTransfer)) {
                        // user un-selected, so remove the video from the collection of selected videos
                        setIsViewSelected(true)
                        videoLayoutItemVideoSelectedCheckBox.isChecked = true
                    } else {
                        // user selects, so add the application to the collection of selected videos
                        setIsViewSelected(false)
                        videoLayoutItemVideoSelectedCheckBox.isChecked = false
                    }
                }

                if (selectedVideos.contains(dataToTransfer)) {
                    setIsViewSelected(true)
                    videoLayoutItemVideoSelectedCheckBox.isChecked = true
                } else {
                    setIsViewSelected(false)
                    videoLayoutItemVideoSelectedCheckBox.isChecked = false
                }
            }
            executePendingBindings()
        }
    }

    companion object {
        fun createViewHolder(
            parent: ViewGroup,
            dataToTransferRecyclerViewItemClickListener: DataToTransferRecyclerViewItemClickListener<DataToTransfer>
        ): VideoLayoutItemViewHolder {
            val layoutBinding = DataBindingUtil.inflate<VideoLayoutItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.video_layout_item,
                parent,
                false
            )
            return VideoLayoutItemViewHolder(
                layoutBinding,
                dataToTransferRecyclerViewItemClickListener
            )
        }
    }
}