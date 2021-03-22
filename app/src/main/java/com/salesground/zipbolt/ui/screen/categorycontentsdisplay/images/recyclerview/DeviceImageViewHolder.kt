package com.salesground.zipbolt.ui.screen.categorycontentsdisplay.images.recyclerview

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.salesground.zipbolt.R
import com.salesground.zipbolt.databinding.ImageLayoutItemBinding
import com.salesground.zipbolt.ui.screen.categorycontentsdisplay.images.dto.ImagesDisplayModel

val imagePlaceHolder = ColorDrawable(Color.LightGray.copy(alpha = 0.5f).toArgb())

class DeviceImageViewHolder(
    private val imageLayoutItemBinding:
    ImageLayoutItemBinding
) : RecyclerView.ViewHolder(imageLayoutItemBinding.root) {


    fun bindImageDisplay(data: ImagesDisplayModel.DeviceImageDisplay) {
        imageLayoutItemBinding.apply {
            Glide.with(root.context)
                .load(data.deviceImage.imageUri)
                .placeholder(imagePlaceHolder)
                .into(deviceImageDisplayImageView)

            deviceImageDisplayImageView.setOnClickListener {
                val scaleAnimation = ValueAnimator.ofFloat(1f, 0.7f).apply {
                    duration = 400
                   // start()
                }
                scaleAnimation.addUpdateListener {
                    deviceImageDisplayImageView.scaleX = it.animatedValue as Float
                    deviceImageDisplayImageView.scaleY = it.animatedValue as Float
                }

                val colorAnimation = ValueAnimator
                    .ofArgb(Color.Transparent.toArgb(), Color.Blue.copy(alpha = 0.1f).toArgb()).apply {
                        duration = 400
                       // start()
                    }
                colorAnimation.addUpdateListener {
                    deviceImageDisplayImageViewGroup.foreground =
                        ColorDrawable(it.animatedValue as Int)
                }
                AnimatorSet().apply {
                    playTogether(scaleAnimation, colorAnimation)
                    start()
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