package com.salesground.zipbolt.ui.customviews

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import com.salesground.zipbolt.R
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class SearchingForDevicesAnimatedView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var numberOfOuterCircles = 4
    val radiusIncrement : Float = 1 / numberOfOuterCircles.toFloat()
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
        alpha = 20
    }
    private val coreCirclePaint = Paint().apply{
        style = Paint.Style.FILL
       // color = ContextCompat.getColor(context, R.color.blue_415)
        isDither = true
        shader = LinearGradient(0f, 0f, 100f, 0f, intArrayOf(ContextCompat.getColor(context, R.color.blue_415),
        ContextCompat.getColor(context, R.color.purple_200)), null, Shader.TileMode.MIRROR)
        isAntiAlias = true
        alpha = 80
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
        var currentRadius : Float  = 0f
        for(i in 1 .. 4){
            currentRadius += radiusIncrement
            canvas.drawCircle(
                center.x, center.y,  max(baseRadius  + (baseRadius * 0.2f), radius * currentRadius),
                circlePaint
            )
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        center = PointF(w * 0.5f, h * 0.5f)
        baseRadius = w * 0.10f

        ValueAnimator.ofFloat(baseRadius  + (baseRadius * 0.2f), w * 0.45f).apply {
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