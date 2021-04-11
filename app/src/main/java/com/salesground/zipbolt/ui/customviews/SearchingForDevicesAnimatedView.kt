package com.salesground.zipbolt.ui.customviews

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import com.salesground.zipbolt.R
import kotlin.math.roundToInt

class SearchingForDevicesAnimatedView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var center = PointF(0f, 0f)
    private val personDrawable: Drawable = ContextCompat
        .getDrawable(context, R.drawable.person_icon)!!.apply {
            setTint(ContextCompat.getColor(context, R.color.white))
        }

    var radius = 20f * resources.displayMetrics.density
    var baseRadius: Float = 0f

    private val circlePaint = Paint().apply {
        style = Paint.Style.FILL
        color = ContextCompat.getColor(context, R.color.blue_415)
        isDither = true
        isAntiAlias = true
        alpha = 30
    }
    private val coreCirclePaint = Paint().apply{
        style = Paint.Style.FILL
        color = ContextCompat.getColor(context, R.color.blue_415)
        isDither = true
        isAntiAlias = true
        alpha = 90
    }


    override fun onDraw(canvas: Canvas?) {
        canvas?.let {
            drawCircles(canvas = canvas)
            drawCore(canvas = canvas)
        }
    }

    private fun drawCore(canvas: Canvas) {

        // draw base circle
        canvas.drawCircle(center.x, center.y, baseRadius + (baseRadius * 0.2f), coreCirclePaint)

        // draw person drawable
        val drawableLeft = (center.x - baseRadius).roundToInt()
        val drawableTop = (center.y - baseRadius).roundToInt()
        personDrawable.setBounds(
            drawableLeft,
            drawableTop,
            drawableLeft + (baseRadius.roundToInt() * 2),
            drawableTop + (baseRadius.roundToInt() * 2)
        )
        personDrawable.draw(canvas)

    }

    private fun drawCircles(canvas: Canvas) {

        canvas.drawCircle(
            center.x, center.y, radius,
            circlePaint
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        center = PointF(w * 0.5f, h * 0.5f)
        baseRadius = w * 0.10f

        ValueAnimator.ofFloat(baseRadius  + (baseRadius * 0.2f), w * 0.4f).apply {
            repeatMode = ValueAnimator.REVERSE
            repeatCount = ValueAnimator.INFINITE
            duration = 1000
            start()
        }.addUpdateListener {
            radius = it.animatedValue as Float
            invalidate()
        }
    }
}