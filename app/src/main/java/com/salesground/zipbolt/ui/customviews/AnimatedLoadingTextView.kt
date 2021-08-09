package com.salesground.zipbolt.ui.customviews

import android.animation.ValueAnimator
import android.animation.ValueAnimator.*
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import com.google.android.material.textview.MaterialTextView

class AnimatedLoadingTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : MaterialTextView(context, attrs) {
    private var initialTextSize: Int = 0
    private val initialText: String = text.toString()
    private val threeDots: String = "$initialText..."
    private val twoDots: String = "$initialText.."
    private val oneDot: String = "$initialText."

    init {
        animateText()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }

    fun animateText() {
        initialTextSize = text.length
        val valueAnimator: ValueAnimator = ofInt(0, 4).apply {
            repeatMode = REVERSE
            repeatCount = INFINITE
            duration = 2500
            start()
        }
        valueAnimator.addUpdateListener {
            text = when (it.animatedValue) {
                0 -> {
                    initialText
                }
                1 -> {
                    oneDot
                }
                2 -> {
                    twoDots
                }
                3 -> {
                    threeDots
                }
                else -> {
                    threeDots
                }
            }
        }
    }
}