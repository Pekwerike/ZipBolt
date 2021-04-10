package com.salesground.zipbolt.ui.customviews

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import com.salesground.zipbolt.R

class SearchingForDevicesAnimatedView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var center = PointF(0f, 0f)

    var radius = 20f * resources.displayMetrics.density

    val circlePaint = Paint().apply {
        style = Paint.Style.FILL
        color = ContextCompat.getColor(context, R.color.blue_415)
        isDither = true
        isAntiAlias = true
        setAlpha(0.5f)
    }


    override fun onDraw(canvas: Canvas?) {
        canvas?.let {
            drawCircles(canvas = canvas)
        }
    }

    private fun drawCircles(canvas: Canvas) {
        circlePaint.apply { setAlpha(0.5f) }
        canvas.drawCircle(
            center.x, center.y, radius,
            circlePaint
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        center = PointF(w * 0.5f, h * 0.5f)
        ValueAnimator.ofFloat(0f, w * 0.3f).apply {
            repeatMode = ValueAnimator.RESTART
            repeatCount = ValueAnimator.INFINITE
            duration = 1000
            start()
        }.addUpdateListener {
            radius = it.animatedValue as Float
            invalidate()
        }
    }
}