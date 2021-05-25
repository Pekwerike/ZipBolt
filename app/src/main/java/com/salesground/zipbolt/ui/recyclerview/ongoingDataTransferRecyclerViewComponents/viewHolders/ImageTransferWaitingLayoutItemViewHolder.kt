package com.salesground.zipbolt.ui.recyclerview.ongoingDataTransferRecyclerViewComponents.viewHolders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.salesground.zipbolt.databinding.ImageTransferLayoutItemBinding
import com.salesground.zipbolt.model.DataToTransfer

class ImageTransferWaitingLayoutItemViewHolder(
    private val imageTransferLayoutItemBinding: ImageTransferLayoutItemBinding
) : RecyclerView.ViewHolder(imageTransferLayoutItemBinding.root) {

    fun bindImageData(imageData: DataToTransfer) {
        imageTransferLayoutItemBinding.apply {
            Glide.with(imageWaitingForTransferLayoutItemImageView)
                .load(imageData.dataUri)
                .into(imageWaitingForTransferLayoutItemImageView)
        }
    }

    companion object {
        fun createViewHolder(parent: ViewGroup): ImageTransferWaitingLayoutItemViewHolder {
            val layoutBinding = ImageTransferLayoutItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return ImageTransferWaitingLayoutItemViewHolder(layoutBinding)
        }
    }
}