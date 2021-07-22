package com.salesground.zipbolt.ui.recyclerview.ongoingDataTransferRecyclerViewComponents.viewHolders.audio

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.salesground.zipbolt.R
import com.salesground.zipbolt.databinding.VideoTransferLayoutItemBinding
import com.salesground.zipbolt.model.DataToTransfer

class AudioTransferCompleteLayoutItemViewHolder(
    private val audioTransferLayoutItemBinding: VideoTransferLayoutItemBinding
) : RecyclerView.ViewHolder(audioTransferLayoutItemBinding.root) {

    fun bindData(dataToTransfer: DataToTransfer) {
        dataToTransfer as DataToTransfer.DeviceAudio
        audioTransferLayoutItemBinding.run {
            videoSize = dataToTransfer.audioSize
            videoDuration = dataToTransfer.audioDuration
            videoName = dataToTransfer.audioDisplayName

            videoTransferLayoutItemShimmer.run {
                stopShimmer()
                hideShimmer()
            }

            Glide.with(videoTransferLayoutItemVideoPreviewImageView)
                .load(dataToTransfer.audioArtPath)
                .error(R.drawable.ic_baseline_music_note_24)
                .into(videoTransferLayoutItemVideoPreviewImageView)

        }
    }

    companion object {
        fun createViewHolder(parent: ViewGroup): AudioTransferCompleteLayoutItemViewHolder {
            val layoutBinding = DataBindingUtil.inflate<VideoTransferLayoutItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.video_transfer_layout_item,
                parent,
                false
            )
            return AudioTransferCompleteLayoutItemViewHolder(layoutBinding)
        }
    }
}