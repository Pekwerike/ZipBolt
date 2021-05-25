package com.salesground.zipbolt.ui.recyclerview.ongoingDataTransferRecyclerViewComponents.viewHolders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.salesground.zipbolt.R
import com.salesground.zipbolt.databinding.ImageTransferLayoutItemBinding
import com.salesground.zipbolt.databinding.ImageTransferOrReceiveCompleteLayoutItemBinding
import com.salesground.zipbolt.model.DataToTransfer

class ImageTransferOrReceiveCompleteLayoutViewHolder(
    private val imageTransferLayoutItemBinding: ImageTransferLayoutItemBinding
) : RecyclerView.ViewHolder(imageTransferLayoutItemBinding.root) {

    fun bindImageData(dataToTransfer: DataToTransfer) {
        imageTransferLayoutItemBinding.apply {
            imageWaitingForTransferLayoutItemImageView.alpha = 1f
            Glide.with(imageWaitingForTransferLayoutItemImageView)
                .load(dataToTransfer.dataUri)
                .into(imageWaitingForTransferLayoutItemImageView)
        }
    }

    companion object {
        fun createViewHolder(parent: ViewGroup): ImageTransferOrReceiveCompleteLayoutViewHolder {
            val layoutBinding =
                ImageTransferLayoutItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )

            return ImageTransferOrReceiveCompleteLayoutViewHolder(layoutBinding)
        }
    }
}