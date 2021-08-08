package com.salesground.zipbolt.ui.recyclerview.sentDataFragment.viewHolders.audio

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.salesground.zipbolt.R
import com.salesground.zipbolt.databinding.AudioTransferLayoutItemBinding
import com.salesground.zipbolt.model.DataToTransfer

class AudioTransferWaitingLayoutItemViewHolder(
    private val audioTransferLayoutItemBinding: AudioTransferLayoutItemBinding
) : RecyclerView.ViewHolder(audioTransferLayoutItemBinding.root) {

    fun bindData(dataToTransfer: DataToTransfer) {
        dataToTransfer as DataToTransfer.DeviceAudio
        audioTransferLayoutItemBinding.run {
            this.dataToTransfer = dataToTransfer

            Glide.with(audioTransferLayoutItemVideoPreviewImageView)
                .load(dataToTransfer.dataUri)
                .error(R.drawable.ic_baseline_music_note_24)
                .into(audioTransferLayoutItemVideoPreviewImageView)
        }
    }

    companion object {
        fun createViewHolder(parent: ViewGroup): AudioTransferWaitingLayoutItemViewHolder {
            val layoutBinding = DataBindingUtil.inflate<AudioTransferLayoutItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.audio_transfer_layout_item,
                parent,
                false
            )
            return AudioTransferWaitingLayoutItemViewHolder(layoutBinding)
        }
    }
}