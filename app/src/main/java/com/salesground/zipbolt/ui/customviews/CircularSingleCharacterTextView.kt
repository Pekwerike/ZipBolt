package com.salesground.zipbolt.ui.customviews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.Gravity
import androidx.core.content.ContextCompat
import com.salesground.zipbolt.R
import kotlin.math.max
import kotlin.math.roundToInt

class CircularSingleCharacterTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : androidx.appcompat.widget.AppCompatTextView(context, attrs, defStyleAttr) {
    private val minHeight = resources.displayMetrics.density * 54
    private val minWidth = minHeight
    private var circlePath: Path? = null
    private var circleRadius: Float = 30 * resources.displayMetrics.density
    private val backgroundColorChoices: Array<Int> = arrayOf(
        ContextCompat.getColor(
            context, R.color.green_dark
        ),
        ContextCompat.getColor(
            context, R.color.yellow_orange_dark
        ), ContextCompat.getColor(
            context, R.color.red_orange_dark
        ),
        ContextCompat.getColor(
            context, R.color.yellow_bright_dark
        ),
        ContextCompat.getColor(
            context, R.color.red_orange
        )
    )
    private var backgroundPaint: Paint = Paint().apply {
        color = backgroundColorChoices.random()
        isAntiAlias = true
        isDither = true
    }

    init {
        gravity = Gravity.CENTER
        textAlignment = TEXT_ALIGNMENT_CENTER
        setTextColor(Color.WHITE)
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.let {
            clipBackgroundCircle(it)
            drawBackgroundColor(it)
        }
        super.onDraw(canvas)
    }

    private fun clipBackgroundCircle(canvas: Canvas) {
        circlePath?.let {
            canvas.clipPath(it)
        }
    }

    private fun drawBackgroundColor(canvas: Canvas) {
        canvas.drawPaint(backgroundPaint)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        circleRadius = w * 0.4f
        circlePath = Path().apply {
            addCircle(w / 2f, h / 2f, circleRadius, Path.Direction.CCW)
        }
         text = if(text.length > 2 ) {
            text.subSequence(0, 2)
        }else {
            "U"
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = resolveSize(minWidth.roundToInt(), widthMeasureSpec)
        val height = resolveSize(minHeight.roundToInt(), heightMeasureSpec)
        setMeasuredDimension(max(width, height), max(width, height))
    }
}