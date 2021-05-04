package com.salesground.zipbolt.ui.bindingadapters

import android.os.Build
import android.text.Html
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.google.android.material.textview.MaterialTextView
import com.salesground.zipbolt.R

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
fun TextView.setNumberOfDevicesFoundText(numberOfDevicesFound: Int) {
    text = if (numberOfDevicesFound > 0) {
        SpannableStringBuilder().apply {
            append("$numberOfDevicesFound devices found").apply {
                setSpan(
                    ForegroundColorSpan(
                        ContextCompat.getColor(rootView.context, R.color.orange_300)
                    ), 0, 1, SpannableString.SPAN_EXCLUSIVE_INCLUSIVE
                )
            }
        }
    } else {
        "0 devices found"
    }
}

@BindingAdapter("setConnectedDeviceName", "setConnectedDeviceIpAddress", requireAll = true)
fun TextView.setConnectedDeviceDetails(deviceName: String, deviceAddress: String) {
    var offset = 0
    val spannableStringBuilder = SpannableStringBuilder().apply {
        append("Name: $deviceName \n")
        offset += "Name: $deviceName \n".length
        append("Ip Address: $deviceAddress \n")
        offset += "Ip Address: $deviceAddress \n".length
        append("Status: Connected")
        offset += "Status: ".length
        setSpan(
            ForegroundColorSpan(ContextCompat.getColor(rootView.context, R.color.green_500)),
            offset, offset + "Connected".length, SpannableString.SPAN_INCLUSIVE_INCLUSIVE
        )
    }
    text = spannableStringBuilder
}
