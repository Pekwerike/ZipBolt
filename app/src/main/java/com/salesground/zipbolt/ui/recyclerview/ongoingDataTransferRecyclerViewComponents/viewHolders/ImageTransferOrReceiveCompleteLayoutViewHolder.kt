package com.salesground.zipbolt.ui.recyclerview.ongoingDataTransferRecyclerViewComponents.viewHolders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.salesground.zipbolt.R
import com.salesground.zipbolt.databinding.ImageTransferOrReceiveCompleteLayoutItemBinding
import com.salesground.zipbolt.model.DataToTransfer

class ImageTransferOrReceiveCompleteLayoutViewHolder(
    private val imageTransferOrReceiveCompleteLayoutItemBinding: ImageTransferOrReceiveCompleteLayoutItemBinding
) : RecyclerView.ViewHolder(imageTransferOrReceiveCompleteLayoutItemBinding.root) {

    fun bindImageData(dataToTransfer: DataToTransfer) {
        imageTransferOrReceiveCompleteLayoutItemBinding.apply {
            imageSize = "${dataToTransfer.dataSize}mb"
            Glide.with(imageTransferOrReceiveCompleteLayoutItemImageView)
                .load(dataToTransfer.dataUri)
                .into(imageTransferOrReceiveCompleteLayoutItemImageView)
            executePendingBindings()
        }
    }

    companion object {
        fun createViewHolder(parent: ViewGroup): ImageTransferOrReceiveCompleteLayoutViewHolder {
            val layoutBinding =
                DataBindingUtil.inflate<ImageTransferOrReceiveCompleteLayoutItemBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.image_transfer_or_receive_complete_layout_item,
                    parent,
                    false
                )

            return ImageTransferOrReceiveCompleteLayoutViewHolder(layoutBinding)
        }
    }
}