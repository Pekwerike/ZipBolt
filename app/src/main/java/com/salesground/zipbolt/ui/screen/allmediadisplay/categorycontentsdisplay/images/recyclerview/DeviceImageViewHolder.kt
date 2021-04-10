package com.salesground.zipbolt.ui.screen.allmediadisplay.categorycontentsdisplay.images.recyclerview


import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
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
import com.salesground.zipbolt.ui.screen.allmediadisplay.categorycontentsdisplay.images.dto.ImagesDisplayModel
import com.salesground.zipbolt.ui.screen.allmediadisplay.categorycontentsdisplay.images.recyclerview.DeviceImageViewHolderConstants.imagePlaceHolder

object DeviceImageViewHolderConstants {
    val imagePlaceHolder = ColorDrawable(Color.LightGray.copy(alpha = 0.5f).toArgb())
    //val clickedForegroundOverlayColor = Color(android.graphics.Color.parseColor("#38A377")).copy(alpha = 0.4f).toArgb()
    val clickedForegroundOverlayColor = Color(46, 157, 255).copy(alpha = 0.4f).toArgb()
    val unClickedForegroundOverlayColor = Color.Transparent.toArgb()
    val clickedForegroundOverlayDrawable = ColorDrawable(clickedForegroundOverlayColor)
    val unClickedForegroundOverlayDrawable = ColorDrawable(unClickedForegroundOverlayColor)
}

class DeviceImageViewHolder(
    private val imageLayoutItemBinding:
    ImageLayoutItemBinding
) : RecyclerView.ViewHolder(imageLayoutItemBinding.root) {

    fun bindImageDisplay(
        data: ImagesDisplayModel.DeviceImageDisplay,
        onClick: (ImagesDisplayModel.DeviceImageDisplay) -> Unit,
        clickedImages: ArrayMap<ImagesDisplayModel, Boolean>
    ) {
        imageLayoutItemBinding.apply {
            Glide.with(root.context)
                .load(data.deviceImage.imageUri)
                .placeholder(imagePlaceHolder)
                .into(deviceImageDisplayImageView)

            if (clickedImages.contains(data)) {
                deviceImageDisplayImageView.scaleX = 0.7f
                deviceImageDisplayImageView.scaleY = 0.7f
                deviceImageDisplayImageViewGroup.foreground =
                    DeviceImageViewHolderConstants.clickedForegroundOverlayDrawable

            } else {
                deviceImageDisplayImageView.scaleX = 1f
                deviceImageDisplayImageView.scaleY = 1f
                deviceImageDisplayImageViewGroup.foreground =
                    DeviceImageViewHolderConstants.unClickedForegroundOverlayDrawable
            }

            deviceImageDisplayImageViewGroup.setOnClickListener {
                if (clickedImages.contains(data)) {
                    deviceImageDisplayImageView.scaleUpAnimation(
                        parent = deviceImageDisplayImageViewGroup,
                        currentParentForegroundColor = DeviceImageViewHolderConstants.unClickedForegroundOverlayColor
                    )
                } else {
                    deviceImageDisplayImageView.scaleDownAnimation(
                        parent = deviceImageDisplayImageViewGroup,
                        targetParentForegroundValue = DeviceImageViewHolderConstants.clickedForegroundOverlayColor
                    )
                }
                onClick(data)
            }
            executePendingBindings()
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