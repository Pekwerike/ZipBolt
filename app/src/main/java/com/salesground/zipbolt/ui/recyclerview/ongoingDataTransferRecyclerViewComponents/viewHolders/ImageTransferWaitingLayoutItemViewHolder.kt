package com.salesground.zipbolt.ui.recyclerview.ongoingDataTransferRecyclerViewComponents.viewHolders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.salesground.zipbolt.R
import com.salesground.zipbolt.databinding.ImageTransferWaitingLayoutItemBinding
import com.salesground.zipbolt.model.DataToTransfer

class ImageTransferWaitingLayoutItemViewHolder(
    private val imageTransferWaitingLayoutItemBinding: ImageTransferWaitingLayoutItemBinding
) : RecyclerView.ViewHolder(imageTransferWaitingLayoutItemBinding.root) {

    fun bindImageData(imageData: DataToTransfer) {
        imageTransferWaitingLayoutItemBinding.apply {
            dataSize = "${imageData.dataSize}mb" + "mb"
            Glide.with(imageWaitingForTransferLayoutItemImageView)
                .load(imageData.dataUri)
                .into(imageWaitingForTransferLayoutItemImageView)
            executePendingBindings()
        }
    }

    companion object {
        fun createViewHolder(parent: ViewGroup): ImageTransferWaitingLayoutItemViewHolder {
            val layoutBinding = DataBindingUtil.inflate<ImageTransferWaitingLayoutItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.image_transfer_waiting_layout_item,
                parent,
                false
            )
            return ImageTransferWaitingLayoutItemViewHolder(layoutBinding)
        }
    }
}