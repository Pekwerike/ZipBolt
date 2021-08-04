package com.salesground.zipbolt.ui.recyclerview.ongoingDataTransferRecyclerViewComponents.viewHolders.image

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.salesground.zipbolt.databinding.ImageTransferLayoutItemBinding
import com.salesground.zipbolt.model.DataToTransfer

class ImageReceiveCompleteLayoutViewHolder(
    private val imageReceiveLayoutItemBinding: ImageTransferLayoutItemBinding
) : RecyclerView.ViewHolder(imageReceiveLayoutItemBinding.root) {

    fun bindImageData(dataToTransfer: DataToTransfer) {
        imageReceiveLayoutItemBinding.run {
            Glide.with(imageWaitingForTransferLayoutItemImageView)
                .load(dataToTransfer.dataUri)
                .into(imageWaitingForTransferLayoutItemImageView)

            imageTransferLayoutItemLoadingImageShimmer.run {
                stopShimmer()
                hideShimmer()
            }
        }
    }

    companion object {
        fun createViewHolder(parent: ViewGroup): ImageReceiveCompleteLayoutViewHolder {
            val layoutBinding =
                ImageTransferLayoutItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )

            return ImageReceiveCompleteLayoutViewHolder(layoutBinding)
        }
    }
}