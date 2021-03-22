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
import androidx.collection.ArrayMap
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
import com.salesground.zipbolt.ui.screen.categorycontentsdisplay.images.recyclerview.DeviceImageViewwHolderConstants.imagePlaceHolder

object DeviceImageViewwHolderConstants{
    val imagePlaceHolder = ColorDrawable(Color.LightGray.copy(alpha = 0.5f).toArgb())
    val clickedForegroundOverlayColor = Color.Blue.copy(0.12f).toArgb()
    val clickedForegroundOverlayDrawable = ColorDrawable(clickedForegroundOverlayColor)
    val unClickedForegroundOverlay = Color.Transparent.toArgb()
    val unClickedForegroundOverlayDrawable = ColorDrawable(unClickedForegroundOverlay)
}
class DeviceImageViewHolder(
    private val imageLayoutItemBinding:
    ImageLayoutItemBinding
) : RecyclerView.ViewHolder(imageLayoutItemBinding.root) {

    fun bindImageDisplay(
        data: ImagesDisplayModel.DeviceImageDisplay,
        onClick : (ImagesDisplayModel.DeviceImageDisplay) -> Unit,
        clickedImages : ArrayMap<ImagesDisplayModel, Boolean>
    ) {
        imageLayoutItemBinding.apply {
            Glide.with(root.context)
                .load(data.deviceImage.imageUri)
                .placeholder(imagePlaceHolder)
                .into(deviceImageDisplayImageView)

            if (clickedImages.contains(data)) {
                deviceImageDisplayImageView.scaleX = 0.7f
                deviceImageDisplayImageView.scaleY = 0.7f
                deviceImageDisplayImageViewGroup.foreground = DeviceImageViewwHolderConstants.clickedForegroundOverlayDrawable

            } else {
                deviceImageDisplayImageView.scaleX = 1f
                deviceImageDisplayImageView.scaleY = 1f
                deviceImageDisplayImageViewGroup.foreground = DeviceImageViewwHolderConstants.unClickedForegroundOverlayDrawable
            }

            deviceImageDisplayImageView.setOnClickListener {
                if (clickedImages.contains(data)) {
                    deviceImageDisplayImageView.scaleUpAnimation(parent= deviceImageDisplayImageViewGroup,
                    currentParentForegroundColor = DeviceImageViewwHolderConstants.clickedForegroundOverlayColor)
                } else {
                    deviceImageDisplayImageView.scaleDownAnimation(parent = deviceImageDisplayImageViewGroup,
                    targetParentForegroundValue = DeviceImageViewwHolderConstants.unClickedForegroundOverlay)
                }
                onClick(data)
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