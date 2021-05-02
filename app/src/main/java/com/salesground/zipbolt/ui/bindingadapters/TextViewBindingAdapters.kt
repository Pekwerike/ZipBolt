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
    text = if (numberOfDevicesFound >= 0) {
        val spannableString = SpannableStringBuilder().apply {
            append("$numberOfDevicesFound").setSpan(
                ForegroundColorSpan(
                    ContextCompat.getColor(rootView.context, R.color.orange_300)
                ), 0, 1, SpannableString.SPAN_EXCLUSIVE_INCLUSIVE
            )
            append(" devices found")
        }
        spannableString
    } else {
        "0 devices found"
    }
}
