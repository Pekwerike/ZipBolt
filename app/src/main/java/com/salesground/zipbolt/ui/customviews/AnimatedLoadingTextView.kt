package com.salesground.zipbolt.ui.customviews

import android.animation.ValueAnimator
import android.animation.ValueAnimator.*
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.google.android.material.textview.MaterialTextView

class AnimatedLoadingTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : MaterialTextView(context, attrs) {
    private var initialTextSize: Int = 0
    private var initialText: String = text.toString()
    private var threeDots: String = "$initialText..."
    private var twoDots: String = "$initialText.."
    private var oneDot: String = "$initialText."
    private var textAnimator: ValueAnimator? = null

    init {
        animateText()
        if (visibility == View.INVISIBLE) {
            textAnimator?.cancel()
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (visibility != View.INVISIBLE) {
            textAnimator?.start()
        }
    }

    fun setAnimatedText(newText: String) {
        initialText = newText
        threeDots = "$initialText..."
        twoDots = "$initialText.."
        oneDot = "$initialText."
    }


    private fun animateText() {
        initialTextSize = text.length
        textAnimator = ofInt(0, 4).apply {
            repeatMode = REVERSE
            repeatCount = INFINITE
            duration = 2500
            start()
        }
        textAnimator?.addUpdateListener {
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