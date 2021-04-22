package com.salesground.zipbolt.ui.bindingadapters

import android.os.Build
import android.text.Html
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.google.android.material.textview.MaterialTextView
import com.salesground.zipbolt.R

@BindingAdapter("addGreenHighLightToText")
fun MaterialTextView.addGreenHighLightToText(placeHolder: String?) {
    val spannableString = SpannableString(placeHolder).apply {
        setSpan(ForegroundColorSpan(ContextCompat.getColor(context,
        R.color.blue_415)),8, 24, SpannableString.SPAN_EXCLUSIVE_INCLUSIVE)
    }
    text = spannableString
}