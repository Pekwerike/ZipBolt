package com.salesground.zipbolt.ui.customviews

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.CornerPathEffect
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import com.salesground.zipbolt.R

class SelectableLinearLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayoutCompat(context, attrs) {
    private var isViewSelected = false
    private val cornerRect = RectF()
    private val colorLightBlue = ContextCompat.getColor(context, R.color.blue_415)
    private val colorLightBlueTransparent =
        ContextCompat.getColor(context, R.color.blue_415_15_percent_alpha)
    private val colorTransparent = ContextCompat.getColor(context, android.R.color.transparent)

    private val cornerRectRadius = 4 * resources.displayMetrics.density
    private val cornerRectStrokePaint = Paint().apply {
        style = Paint.Style.STROKE
        isAntiAlias = true
        isDither = true
        strokeWidth = 4 * resources.displayMetrics.density
        color = colorTransparent
        pathEffect = CornerPathEffect(cornerRectRadius)
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }

    private val cornerRectFillPaint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
        color = colorTransparent
        isDither = true

    }

    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)
        if (isViewSelected) {
            canvas?.let {
                drawScrim(it)
                drawRoundedCorners(it)
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        cornerRect.apply {
            left = 0f
            top = 0f
            right = measuredWidth.toFloat()
            bottom = measuredHeight.toFloat()
        }
    }

    private fun drawScrim(canvas: Canvas) {
        canvas.drawRect(
            cornerRect,
            cornerRectFillPaint
        )
    }

    private fun drawRoundedCorners(canvas: Canvas) {
        canvas.drawRect(
            cornerRect,
            cornerRectStrokePaint
        )
    }

    fun setIsViewSelected(selected: Boolean) {
        if (isViewSelected && !selected) {

            val strokeColorAnimator = ValueAnimator.ofInt(colorLightBlue, colorTransparent).apply {
                duration = 500
                addUpdateListener {
                    val animatedColorValue = it.animatedValue as Int
                    cornerRectStrokePaint.color = animatedColorValue
                    invalidate()
                }
            }

            val fillColorAnimator =
                ValueAnimator.ofInt(colorLightBlueTransparent, colorTransparent).apply {
                    duration = 500
                    addUpdateListener {
                        val animatedColorValue = it.animatedValue as Int
                        cornerRectFillPaint.color = animatedColorValue
                    }
                }

            AnimatorSet().run {
                playTogether(strokeColorAnimator, fillColorAnimator)
                start()
            }
            isViewSelected = selected
        } else if (!isViewSelected && selected) {

            val strokeColorAnimator = ValueAnimator.ofInt(colorTransparent, colorLightBlue).apply {
                duration = 600
                addUpdateListener {
                    val animatedColorValue = it.animatedValue as Int
                    cornerRectStrokePaint.color = animatedColorValue
                    invalidate()
                }
            }

            val fillColorAnimator =
                ValueAnimator.ofInt(colorTransparent, colorLightBlueTransparent).apply {
                    duration = 600
                    addUpdateListener {
                        val animatedColorValue = it.animatedValue as Int
                        cornerRectFillPaint.color = animatedColorValue
                    }
                }

            AnimatorSet().run {
                playTogether(strokeColorAnimator, fillColorAnimator)
                start()
            }
            isViewSelected = selected
        }
    }
}