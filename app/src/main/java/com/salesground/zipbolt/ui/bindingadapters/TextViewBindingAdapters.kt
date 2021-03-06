package com.salesground.zipbolt.ui.bindingadapters

import android.graphics.Typeface.BOLD
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.salesground.zipbolt.R
import com.salesground.zipbolt.utils.formatVideoDurationToString
import com.salesground.zipbolt.utils.parseDate
import com.salesground.zipbolt.utils.transformDataSizeToMeasuredUnit
import kotlin.math.roundToInt
import kotlin.math.roundToLong

@BindingAdapter("addGreenHighLightToText")
fun TextView.addGreenHighLightToText(placeHolder: String?) {
    val spannableString = SpannableString(placeHolder).apply {
        setSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(
                    context,
                    R.color.orange_300
                )
            ), 8, 16, SpannableString.SPAN_EXCLUSIVE_INCLUSIVE
        )
    }
    text = spannableString
}

@BindingAdapter("setNumberOfDevicesFoundText")
fun TextView.setNumberOfDevicesFoundText(numberOfDevicesFound: Int?) {
    numberOfDevicesFound?.let {
        text = if (numberOfDevicesFound > 0) {
            SpannableStringBuilder().apply {
                append("$numberOfDevicesFound devices found").apply {
                    setSpan(
                        ForegroundColorSpan(
                            ContextCompat.getColor(rootView.context, R.color.green_500)
                        ), 0, 1, SpannableString.SPAN_EXCLUSIVE_INCLUSIVE
                    )
                }
            }
        } else {
            "0 devices found"
        }
    }
}

@BindingAdapter("setConnectedDeviceName", "setConnectedDeviceIpAddress", requireAll = true)
fun TextView.setConnectedDeviceDetails(deviceName: String?, deviceAddress: String?) {
    deviceName?.let {
        var offset = 0
        var styleOffset = 0
        val spannableStringBuilder = SpannableStringBuilder().apply {
            append("Name: $deviceName \n")
            offset += "Name: $deviceName \n".length
            append("Mac address: $deviceAddress \n")
            offset += "Mac address: $deviceAddress \n".length
            append("Status: Connected")
            offset += "Status: ".length

            styleOffset += "Name: ".length
            setSpan(
                StyleSpan(BOLD),
                styleOffset,
                styleOffset + deviceName.length,
                SpannableString.SPAN_INCLUSIVE_INCLUSIVE
            )

            setSpan(
                ForegroundColorSpan(ContextCompat.getColor(rootView.context, R.color.green_500)),
                offset, offset + "Connected".length, SpannableString.SPAN_INCLUSIVE_INCLUSIVE
            )

        }
        text = spannableStringBuilder
    }
}

@BindingAdapter("setFileSize")
fun TextView.setFolderSize(fileSize: Long?) {
    fileSize?.let {
        text = fileSize.transformDataSizeToMeasuredUnit()
    }
}

@BindingAdapter("setTransferPercent")
fun TextView.setTransferPercent(transferPercent: Int?) {
    transferPercent?.let {
        text = context.getString(R.string.data_percent_transferred, transferPercent)
    }
}

@BindingAdapter(
    "setTransferPercentForFileSize",
    "setTransferredSizeOverFileSize",
    requireAll = true
)
fun TextView.setTransferPercent(transferPercent: Int?, fileSize: Long?) {
    if (transferPercent != null && fileSize != null) {
        text =
            fileSize.transformDataSizeToMeasuredUnit(((transferPercent / 100f) * fileSize).roundToLong())
    }

}

@BindingAdapter("setVideoDuration", "setVideoSize", requireAll = true)
fun TextView.setVideoDurationAndSize(videoDuration: Long?, videoSize: Long?) {
    if (videoDuration != null && videoSize != null) {
        text = context.getString(
            R.string.middle_round_dot_text_separation, videoDuration.formatVideoDurationToString(),
            videoSize.transformDataSizeToMeasuredUnit()
        )
    }
}

@BindingAdapter("setFileLastModified", "setFileSize", requireAll = true)
fun TextView.setFileLastModifiedDateAndSize(fileLastModified: Long?, fileSize: Long?) {
    if (fileLastModified != null && fileSize != null) {
        text = context.getString(
            R.string.middle_round_dot_text_separation,
            fileSize.transformDataSizeToMeasuredUnit(),
            fileLastModified.parseDate()
        )
    }
}

@BindingAdapter("setNumberOfItemSelected", "setTotalSizeOfItemsSelected", requireAll = true)
fun TextView.setNumberOfItemSelectedAndTotalSizeOfItemsSelected(
    numberOfItemsSelected: Int?,
    totalSizeOfItemsSelected: Long?
) {
    if (numberOfItemsSelected != null && totalSizeOfItemsSelected != null) {
        text = context.getString(
            R.string.total_number_of_items_and_size_of_items_selected_placeholder,
            numberOfItemsSelected,
            totalSizeOfItemsSelected.transformDataSizeToMeasuredUnit()
        )
    }
}