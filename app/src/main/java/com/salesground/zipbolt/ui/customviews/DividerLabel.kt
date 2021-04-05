package com.salesground.zipbolt.ui.customviews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import com.google.android.material.textview.MaterialTextView

class DividerLabel @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : MaterialTextView(context, attrs) {

    private val dividerPaint: Paint

    init {
        textAlignment = TEXT_ALIGNMENT_CENTER
        dividerPaint = Paint().apply {
            style = Paint.Style.STROKE
            isAntiAlias = true
            isDither = true
            color = textColors.defaultColor
            strokeWidth = 1f * context.resources.displayMetrics.scaledDensity
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        Log.i("TextSize", textSize.toString())
        canvas?.let {
            drawDividerLine(it)
        }
    }

    private fun drawDividerLine(canvas: Canvas) {
        canvas.drawLine(
            paddingLeft.toFloat(),
            measuredHeight * 0.5f, (measuredWidth * 0.5f) - textSize,
            measuredHeight * 0.5f,
            dividerPaint
        )
        canvas.drawLine(
            measuredWidth * 0.50f + textSize,
            measuredHeight * 0.5f, (measuredWidth - paddingRight).toFloat(),
            measuredHeight * 0.5f,
            dividerPaint
        )
    }
}