package com.salesground.zipbolt.ui.customviews

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
    private var isViewSelected = true
    private val cornerRect = RectF()
    private val cornerRectRadius = 4 * resources.displayMetrics.density
    private var randomlySelectedColorPair = Pair(
        ContextCompat.getColor(context, R.color.blue_415),
        ContextCompat.getColor(context, R.color.blue_415_20_percent_alpha)
    )
    private val selectedColorsList = listOf(
        Pair(
            ContextCompat.getColor(context, R.color.blue_415),
            ContextCompat.getColor(context, R.color.blue_415_20_percent_alpha)
        ),
        Pair(
            ContextCompat.getColor(context, R.color.orange_300),
            ContextCompat.getColor(context, R.color.orange_300_20_percent_alpha)
        )
    )
    private val cornerRectStrokePaint = Paint().apply {
        style = Paint.Style.STROKE
        isAntiAlias = true
        isDither = true
        strokeWidth = 4 * resources.displayMetrics.density
        pathEffect = CornerPathEffect(cornerRectRadius)
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }

    private val cornerRectFillPaint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
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
        randomlySelectedColorPair = selectedColorsList.random()
        cornerRectFillPaint.color = randomlySelectedColorPair.second
        cornerRectStrokePaint.color = randomlySelectedColorPair.first
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
        isViewSelected = selected
        invalidate()
    }

}