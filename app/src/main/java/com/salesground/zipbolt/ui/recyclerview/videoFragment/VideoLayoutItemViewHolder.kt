package com.salesground.zipbolt.ui.recyclerview.videoFragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.salesground.zipbolt.R
import com.salesground.zipbolt.databinding.VideoLayoutItemBinding
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.ui.recyclerview.DataToTransferRecyclerViewItemClickListener

class VideoLayoutItemViewHolder(
    private val videoLayoutItemBinding: VideoLayoutItemBinding,
    private val dataToTransferRecyclerViewItemClickListener: DataToTransferRecyclerViewItemClickListener
) : RecyclerView.ViewHolder(videoLayoutItemBinding.root) {

    fun bindVideoData(
        dataToTransfer: DataToTransfer,
        selectedVideos: MutableList<DataToTransfer>
    ) {
        dataToTransfer as DataToTransfer.DeviceVideo

        videoLayoutItemBinding.run {
            videoLayoutItemSelectableConstraintLayout.run {
                setOnClickListener {
                    dataToTransferRecyclerViewItemClickListener.onClick(
                        dataToTransfer
                    )

                    if (selectedVideos.contains(dataToTransfer)) {
                        // user un-selected, so remove the video from the collection of selected videos
                        setIsViewSelected(false)
                        videoLayoutItemVideoSelectedCheckBox.isChecked = false
                        selectedVideos.remove(dataToTransfer)
                    } else {
                        // user selects, so add the application to the collection of selected videos
                        setIsViewSelected(true)
                        videoLayoutItemVideoSelectedCheckBox.isChecked = true
                        selectedVideos.add(dataToTransfer)
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
            videoLayoutItemSelectableConstraintLayout.setIsViewSelected(true)
            executePendingBindings()
        }
    }

    companion object {
        fun createViewHolder(
            parent: ViewGroup,
            dataToTransferRecyclerViewItemClickListener: DataToTransferRecyclerViewItemClickListener
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