package com.salesground.zipbolt.ui.bindingadapters

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.salesground.zipbolt.R
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.model.MediaType

@BindingAdapter("bindImageForDocument")
fun ImageView.bindImageForDocument(dataToTransfer: DataToTransfer) {

    when (dataToTransfer.dataType) {
        MediaType.App.value -> {

        }
        MediaType.Audio.value -> {

        }
        MediaType.File.Directory.value -> {

        }
        MediaType.File.Document.ExcelDocument.value -> {
            Glide.with(context)
                .load(R.drawable.sheets)
                .into(this)
        }
        MediaType.File.Document.PdfDocument.value -> {
            Glide.with(context)
                .load(R.drawable.pdf)
                .into(this)
        }
        MediaType.File.Document.PowerPointDocument.value -> {
            Glide.with(context)
                .load(R.drawable.slides)
                .into(this)
        }
        MediaType.File.Document.UnknownDocument.value -> {

        }
        MediaType.File.Document.WordDocument.value -> {
            Glide.with(context)
                .load(R.drawable.docs)
                .into(this)
        }
        MediaType.File.Document.ZipDocument.value -> {
            Glide.with(context)
                .load(R.drawable.zip_file)
                .into(this)
        }
        MediaType.Image.value -> {

        }
        MediaType.Video.value -> {

        }
    }
}