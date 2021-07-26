package com.salesground.zipbolt.ui.customviews

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.Glide
import com.salesground.zipbolt.R
import kotlin.math.roundToInt


class CurvedImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : androidx.appcompat.widget.AppCompatImageView(context, attrs) {

    private var roundRectPath = Path()
    private var cornerRadius: Float = 0f
    private var mPaddingLeft: Float = 0f
    private var mPaddingRight: Float = 0f
    private var mPaddingTop: Float = 0f
    private var mPaddingBottom: Float = 0f
    private val surfaceColor: Int = ContextCompat.getColor(context, R.color.surface_color)
    private val basePaint : Paint = Paint().apply {
        color = surfaceColor
        isAntiAlias = true
        isDither = true
        pathEffect = CornerPathEffect(2 * context.resources.displayMetrics.density)
        strokeWidth = 2 * context.resources.displayMetrics.density
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
        style = Paint.Style.STROKE
    }

    init {
        context.withStyledAttributes(attrs, R.styleable.CurvedImageView) {
            cornerRadius = getFloat(R.styleable.CurvedImageView_curvedImageViewCornerRadius, 0f)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        curveDrawingSurface(canvas)
        super.onDraw(canvas)
        canvas?.drawPath(roundRectPath, basePaint)
    }

    private fun curveDrawingSurface(canvas: Canvas?) {
        canvas?.let {
            canvas.clipPath(roundRectPath)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mPaddingBottom = paddingBottom.toFloat()
        mPaddingLeft = paddingLeft.toFloat()
        mPaddingRight = paddingRight.toFloat()
        mPaddingTop = paddingTop.toFloat()
        roundRectPath = Path().apply {
            moveTo(mPaddingLeft, (h * (1 - cornerRadius)) - mPaddingBottom)
            lineTo(mPaddingLeft, (h * cornerRadius) + mPaddingTop)
            quadTo(
                mPaddingLeft, mPaddingTop, (w * cornerRadius) + mPaddingLeft,
                mPaddingTop
            )
            lineTo((w * (1 - cornerRadius)) - mPaddingRight, mPaddingTop)

            quadTo(
                w - mPaddingRight,
                mPaddingTop,
                w - mPaddingRight,
                (h * cornerRadius) + mPaddingTop
            )

            lineTo(w - mPaddingRight, (h * (1 - cornerRadius)) - mPaddingRight)

            quadTo(
                w - mPaddingRight, h - mPaddingBottom,
                (w * (1 - cornerRadius)) - mPaddingRight, h - mPaddingBottom
            )

            lineTo((w * cornerRadius) + mPaddingLeft, h - mPaddingBottom)
            quadTo(
                mPaddingLeft, h - mPaddingBottom,
                mPaddingLeft, (h * (1 - cornerRadius)) - mPaddingBottom
            )
        }

    }
}