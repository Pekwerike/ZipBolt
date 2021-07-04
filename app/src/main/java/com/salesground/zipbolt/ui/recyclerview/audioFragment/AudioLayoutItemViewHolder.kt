package com.salesground.zipbolt.ui.recyclerview.audioFragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.salesground.zipbolt.R
import com.salesground.zipbolt.databinding.VideoLayoutItemBinding
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.ui.recyclerview.DataToTransferRecyclerViewItemClickListener

class AudioLayoutItemViewHolder(
    private val audioLayoutItemBinding: VideoLayoutItemBinding,
    private val dataToTransferRecyclerViewItemClickListener: DataToTransferRecyclerViewItemClickListener
) : RecyclerView.ViewHolder(audioLayoutItemBinding.root) {

    fun bindData(
        dataToTransfer: DataToTransfer,
        selectedAudios: MutableList<DataToTransfer>
    ) {
        dataToTransfer as DataToTransfer.DeviceAudio

        audioLayoutItemBinding.run {
            videoName = dataToTransfer.audioDisplayName
            videoSize = dataToTransfer.audioSize
            videoDuration = dataToTransfer.audioDuration

            Glide.with(videoLayoutItemVideoPreviewImageView)
                .load(dataToTransfer.audioArtPath)
                .into(videoLayoutItemVideoPreviewImageView)



            executePendingBindings()
        }
    }

    companion object {
        fun createViewHolder(
            parent: ViewGroup,
            dataToTransferRecyclerViewItemClickListener: DataToTransferRecyclerViewItemClickListener
        ): AudioLayoutItemViewHolder {
            val layoutBinding = DataBindingUtil.inflate<VideoLayoutItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.video_layout_item,
                parent,
                false
            )

            return AudioLayoutItemViewHolder(
                layoutBinding,
                dataToTransferRecyclerViewItemClickListener
            )
        }
    }
}