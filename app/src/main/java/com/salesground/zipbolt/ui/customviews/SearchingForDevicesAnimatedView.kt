package com.salesground.zipbolt.ui.customviews

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.salesground.zipbolt.R
import kotlin.math.max
import kotlin.math.roundToInt

class SearchingForDevicesAnimatedView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var numberOfOuterCircles = 4
    private val radiusIncrement: Float = 1 / numberOfOuterCircles.toFloat()
    private var center = PointF(0f, 0f)

    private val personDrawable: Drawable = ContextCompat
        .getDrawable(context, R.drawable.person_icon)!!.apply {
            setTint(ContextCompat.getColor(context, R.color.white))
        }
    private var drawableLeft: Int = 0
    private var drawableTop: Int = 0

    private var maxRadius = 20f * resources.displayMetrics.density
    private var baseRadius: Float = 0f
    private var coreCircleRadius: Float = 0f


    private val circlePaint = Paint().apply {
        style = Paint.Style.FILL
        color = ContextCompat.getColor(context, R.color.blue_415)
        isDither = true
        isAntiAlias = true
        alpha = 20
    }

    private val coreCirclePaint = Paint().apply {
        style = Paint.Style.FILL
        //color = ContextCompat.getColor(context, R.color.blue_415)
        isDither = true
        shader = LinearGradient(
            0f, 0f, 100f, 0f, intArrayOf(
                ContextCompat.getColor(context, R.color.blue_415),
                ContextCompat.getColor(context, R.color.purple_200)
            ), null, Shader.TileMode.REPEAT
        )
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

        // draw core circle
        canvas.drawCircle(center.x, center.y, coreCircleRadius, coreCirclePaint)
        personDrawable.draw(canvas)

    }

    private fun drawCircles(canvas: Canvas) {
        var currentRadius = 0f
        for (i in 1..numberOfOuterCircles) {
            currentRadius += radiusIncrement
            canvas.drawCircle(
                center.x, center.y, max(coreCircleRadius, maxRadius * currentRadius),
                circlePaint
            )
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        center = PointF(w * 0.5f, h * 0.5f)
        baseRadius = w * 0.12f
        coreCircleRadius = baseRadius + (baseRadius * 0.2f)

        // set the bounds of the drawable
        drawableLeft = (center.x - baseRadius).roundToInt()
        drawableTop = (center.y - baseRadius).roundToInt()
        personDrawable.setBounds(
            drawableLeft,
            drawableTop,
            drawableLeft + (baseRadius.roundToInt() * 2),
            drawableTop + (baseRadius.roundToInt() * 2)
        )

        ValueAnimator.ofFloat(coreCircleRadius, w * 0.5f).apply {
            repeatMode = ValueAnimator.RESTART
            repeatCount = ValueAnimator.INFINITE
            duration = 1000
            start()
        }.addUpdateListener {
            maxRadius = it.animatedValue as Float
            invalidate()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val drawableWidth = personDrawable.intrinsicWidth
        val drawableHeight = personDrawable.intrinsicHeight

        setMeasuredDimension(
            resolveSize((drawableWidth * 10) + paddingLeft + paddingRight,
        widthMeasureSpec), resolveSize((drawableHeight * 10) + paddingTop + paddingBottom,
                widthMeasureSpec))
    }
}