package com.salesground.zipbolt.ui.bindingadapters

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.salesground.zipbolt.R
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.model.MediaType

@BindingAdapter("bindImageForDocument")
fun ImageView.bindImageForDocument(dataToTransfer: DataToTransfer) {

    when (dataToTransfer.mediaType) {
        MediaType.App -> {

        }
        MediaType.Audio -> {

        }
        MediaType.Directory -> {

        }
        MediaType.DocumentMedia.ExcelMedia -> {
            Glide.with(context)
                .load(R.drawable.sheets)
                .into(this)
        }
        MediaType.DocumentMedia.Pdf -> {
            Glide.with(context)
                .load(R.drawable.pdf)
                .into(this)
        }
        MediaType.DocumentMedia.PowerPointMedia -> {
            Glide.with(context)
                .load(R.drawable.slides)
                .into(this)
        }
        MediaType.DocumentMedia.UnknownMedia -> {

        }
        MediaType.DocumentMedia.WordMedia -> {
            Glide.with(context)
                .load(R.drawable.docs)
                .into(this)
        }
        MediaType.DocumentMedia.ZipMedia -> {
            Glide.with(context)
                .load(R.drawable.zip_file)
                .into(this)
        }
        MediaType.Image -> {

        }
        MediaType.Video -> {

        }
    }
}