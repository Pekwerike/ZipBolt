package com.salesground.zipbolt.ui.bindingadapters

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.salesground.zipbolt.R
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.model.DocumentType

@BindingAdapter("bindImageForDocument")
fun ImageView.bindImageForDocument(dataToTransfer: DataToTransfer) {

    when (dataToTransfer.documentType) {
        DocumentType.App -> {

        }
        DocumentType.Audio -> {

        }
        DocumentType.Directory -> {

        }
        DocumentType.Document.ExcelDocument -> {
            Glide.with(context)
                .load(R.drawable.sheets)
                .into(this)
        }
        DocumentType.Document.Pdf -> {
            Glide.with(context)
                .load(R.drawable.pdf)
                .into(this)
        }
        DocumentType.Document.PowerPointDocument -> {
            Glide.with(context)
                .load(R.drawable.slides)
                .into(this)
        }
        DocumentType.Document.UnknownDocument -> {

        }
        DocumentType.Document.WordDocument -> {
            Glide.with(context)
                .load(R.drawable.docs)
                .into(this)
        }
        DocumentType.Document.ZipDocument -> {
            Glide.with(context)
                .load(R.drawable.zip_file)
                .into(this)
        }
        DocumentType.Image -> {

        }
        DocumentType.Video -> {

        }
    }
}