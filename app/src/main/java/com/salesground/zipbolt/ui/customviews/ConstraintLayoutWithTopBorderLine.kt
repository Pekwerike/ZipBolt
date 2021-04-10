package com.salesground.zipbolt.ui.customviews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.salesground.zipbolt.R

class ConstraintLayoutWithTopBorderLine @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

  private val borderLinePaint = Paint().apply{
        style = Paint.Style.STROKE
        strokeWidth = 1f * resources.displayMetrics.density
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        isDither = true
        isAntiAlias = true
        color = ContextCompat.getColor(context, R.color.onBackgroundColor)
    }
    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)
        canvas?.drawLine(
            paddingLeft.toFloat(), paddingTop.toFloat(),
            (measuredWidth - paddingRight).toFloat(),
            paddingTop.toFloat(),
            borderLinePaint
        )
    }

}