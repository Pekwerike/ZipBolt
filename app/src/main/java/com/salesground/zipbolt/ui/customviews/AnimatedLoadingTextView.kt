package com.salesground.zipbolt.ui.customviews

import android.animation.ValueAnimator
import android.animation.ValueAnimator.INFINITE
import android.animation.ValueAnimator.RESTART
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import com.google.android.material.textview.MaterialTextView

class AnimatedLoadingTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : MaterialTextView(context, attrs) {
    private val stringBuilder = StringBuilder()
    private var initialTextSize: Int = 0

    init {
        stringBuilder.append(text.toString())
        animateText()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }

    fun animateText() {
        initialTextSize = text.length
        stringBuilder.append(text.toString())

        val valueAnimator: ValueAnimator = ValueAnimator.ofInt(0, 3).apply {
            repeatMode = RESTART
            repeatCount = INFINITE
            duration = 1000
            start()
        }
        valueAnimator.addUpdateListener {
            text = when (it.animatedValue) {
                0 -> {
                    stringBuilder.substring(initialTextSize, text.length)
                    stringBuilder
                }
                else -> {
                    stringBuilder.append(".")
                    stringBuilder
                }
            }
        }
    }
}