package com.salesground.zipbolt.ui.bindingadapters

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.salesground.zipbolt.R
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.model.MediaType

@BindingAdapter("bindImageBasedOnMediaType")
fun ImageView.bindImageForDocument(dataToTransfer: DataToTransfer?) {
    dataToTransfer?.let {
        when (dataToTransfer.dataType) {
            MediaType.App.value -> {
                dataToTransfer as DataToTransfer.DeviceApplication
                Glide.with(context)
                    .load(
                        dataToTransfer.applicationIcon
                    )
                    .into(this)
            }
            MediaType.Audio.value -> {
                dataToTransfer as DataToTransfer.DeviceAudio
                Glide.with(context)
                    .load(dataToTransfer.audioArtPath)
                    .error(R.drawable.ic_baseline_music_note_24)
                    .into(this)
            }
            MediaType.File.Directory.value -> {
                Glide.with(context)
                    .load(R.drawable.ic_baseline_folder_open_24)
                    .into(this)
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
                Glide.with(context)
                    .load(dataToTransfer.dataUri)
                    .into(this)
            }
            MediaType.Video.value -> {
                Glide.with(context)
                    .load(dataToTransfer.dataUri)
                    .into(this)
            }
            else -> {

            }
        }
    }
}

