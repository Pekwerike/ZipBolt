package com.salesground.zipbolt.ui.customviews

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import androidx.core.graphics.drawable.toBitmap
import com.salesground.zipbolt.R


class CurvedImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : androidx.appcompat.widget.AppCompatImageView(context, attrs) {

    private var roundRectPath = Path()
    private var cornerRadius: Float = 0f
    private var mPaddingLeft: Float = 0f
    private var mPaddingRight: Float = 0f
    private var mPaddingTop: Float = 0f
    private var mPaddingBottom: Float = 0f


    init {
        context.withStyledAttributes(attrs, R.styleable.CurvedImageView) {
            cornerRadius = getDimension(R.styleable.CurvedImageView_curvedImageViewCornerRadius, 0f)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        curveDrawingSurface(canvas)
        super.onDraw(canvas)
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
            moveTo(mPaddingLeft, (h - mPaddingBottom) * (1 - cornerRadius))
            lineTo(mPaddingLeft, (h - mPaddingTop) * cornerRadius)
            quadTo(
                mPaddingLeft, mPaddingTop, (w * cornerRadius) + mPaddingLeft,
                mPaddingTop
            )
            lineTo((w * 1 - cornerRadius) - mPaddingRight, mPaddingTop)

            quadTo(
                w - mPaddingRight,
                mPaddingTop,
                w - mPaddingRight,
                (h * cornerRadius) - mPaddingTop
            )

            lineTo(w - mPaddingRight, (h * (1 - cornerRadius)) - mPaddingRight)

            quadTo(
                w - mPaddingRight, h - mPaddingBottom,
                (w * 1 - cornerRadius) - mPaddingRight, h - mPaddingBottom
            )

            lineTo((w * cornerRadius) + mPaddingLeft, h - mPaddingBottom)
            quadTo(
                mPaddingLeft, h - mPaddingBottom,
                mPaddingLeft, (h * (1 - cornerRadius)) - mPaddingBottom
            )
        }

    }
}