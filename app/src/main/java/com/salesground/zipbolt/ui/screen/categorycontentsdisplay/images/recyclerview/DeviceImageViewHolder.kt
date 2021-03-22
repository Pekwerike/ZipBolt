package com.salesground.zipbolt.ui.screen.categorycontentsdisplay.images.recyclerview

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.salesground.zipbolt.R
import com.salesground.zipbolt.databinding.ImageLayoutItemBinding
import com.salesground.zipbolt.ui.animationutils.scaleDownAnimation
import com.salesground.zipbolt.ui.animationutils.scaleUpAnimation
import com.salesground.zipbolt.ui.screen.categorycontentsdisplay.images.dto.ImagesDisplayModel

val imagePlaceHolder = ColorDrawable(Color.LightGray.copy(alpha = 0.5f).toArgb())


class DeviceImageViewHolder(
    private val imageLayoutItemBinding:
    ImageLayoutItemBinding
) : RecyclerView.ViewHolder(imageLayoutItemBinding.root) {

    fun bindImageDisplay(
        data: ImagesDisplayModel.DeviceImageDisplay,
        isViewClicked: Boolean = false
    ) {
        imageLayoutItemBinding.apply {
            Glide.with(root.context)
                .load(data.deviceImage.imageUri)
                .placeholder(imagePlaceHolder)
                .into(deviceImageDisplayImageView)

            if (isViewClicked) {
                deviceImageDisplayImageView.scaleX = 0.7f
                deviceImageDisplayImageView.scaleY = 0.7f
                deviceImageDisplayImageViewGroup.foreground =
                    ColorDrawable(Color.Blue.copy(0.12f).toArgb())
            } else {
                deviceImageDisplayImageView.scaleX = 1f
                deviceImageDisplayImageView.scaleY = 1f
                deviceImageDisplayImageViewGroup.foreground =
                    ColorDrawable(Color.Transparent.toArgb())
            }

            deviceImageDisplayImageView.setOnClickListener {
                if (isViewClicked) {
                    deviceImageDisplayImageView.scaleUpAnimation(deviceImageDisplayImageViewGroup)
                } else {
                    deviceImageDisplayImageView.scaleDownAnimation(deviceImageDisplayImageViewGroup)
                }
            }
        }
    }

    companion object {
        fun createDeviceImageViewHolder(parent: ViewGroup): DeviceImageViewHolder {
            val layoutItemBinding =
                DataBindingUtil.inflate<ImageLayoutItemBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.image_layout_item,
                    parent,
                    false
                )
            return DeviceImageViewHolder(layoutItemBinding)
        }
    }
}