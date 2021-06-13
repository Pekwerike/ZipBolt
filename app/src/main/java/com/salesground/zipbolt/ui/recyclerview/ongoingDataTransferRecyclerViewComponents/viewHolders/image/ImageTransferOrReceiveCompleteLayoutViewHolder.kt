package com.salesground.zipbolt.ui.recyclerview.ongoingDataTransferRecyclerViewComponents.viewHolders.image

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.salesground.zipbolt.databinding.ImageTransferOrReceiveCompleteLayoutItemBinding
import com.salesground.zipbolt.model.DataToTransfer

class ImageTransferOrReceiveCompleteLayoutViewHolder(
    private val imageTransferOrReceiveCompleteLayoutItemBinding: ImageTransferOrReceiveCompleteLayoutItemBinding
) : RecyclerView.ViewHolder(imageTransferOrReceiveCompleteLayoutItemBinding.root) {

    fun bindImageData(dataToTransfer: DataToTransfer) {
        imageTransferOrReceiveCompleteLayoutItemBinding.apply {
            Glide.with(imageTransferOrReceiveCompleteLayoutItemImageView)
                .load(dataToTransfer.dataUri)
                .into(imageTransferOrReceiveCompleteLayoutItemImageView)
        }
    }

    companion object {
        fun createViewHolder(parent: ViewGroup): ImageTransferOrReceiveCompleteLayoutViewHolder {
            val layoutBinding =
                ImageTransferOrReceiveCompleteLayoutItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )

            return ImageTransferOrReceiveCompleteLayoutViewHolder(layoutBinding)
        }
    }
}