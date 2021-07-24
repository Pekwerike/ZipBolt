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


    init {
        context.withStyledAttributes(attrs, R.styleable.CurvedImageView) {
            cornerRadius = getDimension(R.styleable.CurvedImageView_cornerRadius, 0f)
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

        roundRectPath = Path().apply {
            addRoundRect(
                paddingLeft.toFloat(),
                paddingTop.toFloat(),
                w.toFloat() - paddingRight.toFloat(),
                h.toFloat() - paddingBottom.toFloat(),
                cornerRadius,
                cornerRadius,
                Path.Direction.CW
            )
        }
    }
}