package com.salesground.zipbolt.ui.customviews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import com.google.android.material.textview.MaterialTextView
import com.salesground.zipbolt.R

class DividerLabel @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : MaterialTextView(context, attrs) {

    private val dividerPaint: Paint

    init {
        context.theme.obtainStyledAttributes(R.styleable.DividerLabel).apply {
            dividerPaint = Paint().apply {
                style = Paint.Style.STROKE
                isAntiAlias = true
                isDither = true
                color = textColors.defaultColor
                strokeWidth = getFloat(R.styleable.DividerLabel_strokeLineHeight, 1f)* context.resources.displayMetrics.scaledDensity
                strokeCap = Paint.Cap.ROUND
                strokeJoin = Paint.Join.ROUND
            }
            recycle()
        }
        textAlignment = TEXT_ALIGNMENT_CENTER

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
           val size = ((textSize / context.resources.displayMetrics.density)
                    * length())
            drawDividerLine(
                canvas = it,
                viewHeight = measuredHeight,
                viewWidth = measuredWidth,
                writtenTextSize = size
            )
        }
    }

    private fun drawDividerLine(
        canvas: Canvas,
        viewHeight: Int, viewWidth: Int,
        writtenTextSize: Float,
    ) {

        canvas.drawLine(
            paddingLeft.toFloat(),
            viewHeight * 0.5f,
            (viewWidth * 0.5f) - writtenTextSize,
            viewHeight * 0.5f,
            dividerPaint
        )
        canvas.drawLine(
            (viewWidth * 0.5f) + writtenTextSize,
            viewHeight * 0.5f, (viewWidth - paddingRight).toFloat(),
            viewHeight * 0.5f,
            dividerPaint
        )
    }

}