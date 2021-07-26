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
import com.google.android.material.animation.AnimatorSetCompat
import com.salesground.zipbolt.R

class SelectableLinearLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayoutCompat(context, attrs) {
    private var isViewSelected = false
    private val cornerRect = RectF()
    private val cornerRectFillPaint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
        isDither = true
        color = ContextCompat.getColor(context, R.color.blue_415_15_percent_alpha)
    }

    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)
        if (isViewSelected) {
            canvas?.let {
                drawScrim(it)
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


    fun setIsViewSelected(selected: Boolean) {
        if (isViewSelected && !selected) {
            isViewSelected = selected
            invalidate()
        } else if (!isViewSelected && selected) {
            isViewSelected = selected
            invalidate()
        }
    }

}