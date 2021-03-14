package com.salesground.zipbolt.ui.screen.homescreen.recyclerviewholder

import android.os.Build
import android.util.Size
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.salesground.zipbolt.R
import com.salesground.zipbolt.databinding.AudioLayoutItemBinding
import com.salesground.zipbolt.ui.screen.homescreen.recyclerviewadapter.datamodel.DataCategory

class HSAudioCategoryViewHolder(
    private val audioLayoutItemBinding: AudioLayoutItemBinding
) : RecyclerView.ViewHolder(audioLayoutItemBinding.root) {

    fun bindAudioData(audioData : DataCategory.Music){
        audioLayoutItemBinding.apply {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                Glide.with(root.context)
                    .load(root.context.
                    contentResolver.
                    loadThumbnail(audioData.musicModel.mediaUri,
                    Size(100, 100),
                        null
                    ))
                    .into(audioArtImageView)
            }else {
                Glide.with(root.context)
                    .load(audioData.musicModel.mediaUri)
                    .into(audioArtImageView)
            }
            audioTitle = audioData.musicModel.mediaDisplayName
        }
    }
    companion object {
        fun createHSAudioCategoryViewHolder(parent: ViewGroup)
                : HSAudioCategoryViewHolder {
            val layoutItemBinding =
                DataBindingUtil.inflate<AudioLayoutItemBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.audio_layout_item,
                    parent,
                    false
                )
            return HSAudioCategoryViewHolder(layoutItemBinding)
        }
    }

}