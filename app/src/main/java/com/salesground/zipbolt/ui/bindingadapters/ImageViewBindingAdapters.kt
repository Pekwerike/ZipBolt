package com.salesground.zipbolt.ui.bindingadapters

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.salesground.zipbolt.R
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.model.MediaType
import java.lang.NullPointerException

@BindingAdapter("bindImageBasedOnMediaType")
fun ImageView.bindImageForDocument(dataToTransfer: DataToTransfer?) {
    dataToTransfer?.let {
        if (it.transferStatus == DataToTransfer.TransferStatus.RECEIVE_STARTED) {
            setImageDrawable(
                AppCompatResources.getDrawable(
                    context, R.drawable.ic_baseline_arrow_circle_down_24
                )
            )
            return
        }
        when (dataToTransfer.dataType) {
            MediaType.App.value -> {
                dataToTransfer as DataToTransfer.DeviceApplication
                Glide.with(context)
                    .load(
                        dataToTransfer.applicationIcon
                    )
                    .error(R.drawable.apk_file_icon)
                    .into(this)
            }
            MediaType.Audio.value -> {
                dataToTransfer as DataToTransfer.DeviceAudio
                Glide.with(context)
                    .load(dataToTransfer.audioArtPath)
                    .error(R.drawable.ic_icons8_music)
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
            MediaType.File.Directory.value -> {
                setImageDrawable(
                    AppCompatResources.getDrawable(
                        context,
                        R.drawable.ic_baseline_folder_open_24
                    )
                )
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
                Glide.with(context)
                    .load(R.drawable.plain_file_icon)
                    .into(this)
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

            MediaType.File.ImageFile.value -> {
                Glide.with(context)
                    .load(dataToTransfer.dataUri)
                    .into(this)
            }

            MediaType.File.AudioFile.value -> {
                Glide.with(context)
                    .load(R.drawable.ic_icons8_music)
                    .into(this)
            }
            MediaType.File.VideoFile.value -> {
                Glide.with(context)
                    .load(dataToTransfer.dataUri)
                    .into(this)
            }

            MediaType.File.AppFile.value -> {
                /*dataToTransfer as DataToTransfer.DeviceFile
                val filePath = dataToTransfer.file.path
                val applicationIcon: Drawable? = try {
                    context.packageManager.getPackageArchiveInfo(
                        filePath, 0
                    ).let { packageInfo ->
                        context.packageManager.getApplicationIcon(
                            packageInfo!!
                                .applicationInfo.apply {
                                    sourceDir = filePath
                                    publicSourceDir = filePath
                                })
                    }
                } catch (nullPointerException: NullPointerException) {
                    null
                }*/
                Glide.with(context)
                    .load(R.drawable.apk_file_icon)
                    .into(this)

            }

            MediaType.File.Document.WebpageDocument.value -> {
                Glide.with(context)
                    .load(R.drawable.ic_google_chrome)
                    .into(this)
            }
            MediaType.File.Document.DatDocument.value -> {
                Glide.with(context)
                    .load(R.drawable.ic_dat_file_logo)
                    .into(this)
            }
            MediaType.File.Document.TextFileDocument.value -> {
                Glide.with(context)
                    .load(R.drawable.text_file_icon)
                    .into(this)
            }

            else -> {

            }
        }
    }
}

