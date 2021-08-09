package com.salesground.zipbolt.ui.recyclerview.ongoingDataTransferRecyclerViewComponents.viewHolders.audio

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.salesground.zipbolt.R
import com.salesground.zipbolt.databinding.AudioTransferLayoutItemBinding
import com.salesground.zipbolt.model.DataToTransfer

class AudioReceiveCompleteLayoutItemViewHolder(
    private val audioTransferLayoutItemBinding: AudioTransferLayoutItemBinding
) : RecyclerView.ViewHolder(audioTransferLayoutItemBinding.root) {

    fun bindData(dataToTransfer: DataToTransfer) {
        dataToTransfer as DataToTransfer.DeviceAudio
        audioTransferLayoutItemBinding.run {
            this.dataToTransfer = dataToTransfer

            audioTransferLayoutItemShimmer.run {
                stopShimmer()
                hideShimmer()
            }

            Glide.with(audioTransferLayoutItemVideoPreviewImageView)
                .load(dataToTransfer.audioArtPath)
                .error(R.drawable.ic_baseline_music_note_24)
                .into(audioTransferLayoutItemVideoPreviewImageView)

        }
    }

    companion object {
        fun createViewHolder(parent: ViewGroup): AudioReceiveCompleteLayoutItemViewHolder {
            val layoutBinding = DataBindingUtil.inflate<AudioTransferLayoutItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.video_transfer_layout_item,
                parent,
                false
            )
            return AudioReceiveCompleteLayoutItemViewHolder(layoutBinding)
        }
    }
}