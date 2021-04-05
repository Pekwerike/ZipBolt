package com.salesground.zipbolt.ui.customviews

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import com.google.android.material.chip.Chip
import com.salesground.zipbolt.R
import kotlin.math.max


class ChipsLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {
    private var maxRowCount = 2
    private val screenWidth = resources.displayMetrics.widthPixels

    init{
        context.theme.obtainStyledAttributes(attrs,
        R.styleable.ChipsLayout, defStyleAttr, 0).apply {
            maxRowCount = getInteger(R.styleable.ChipsLayout_maxRows, 2)
            recycle()
        }
    }

    fun refresh(viewIndex: Int) {
        val child = getChildAt(viewIndex) as Chip
        child.isChecked = false
    }

    override fun onLayout(changed: Boolean, left : Int, top: Int, right: Int, bottom: Int) {
        val leftPadding = paddingLeft
        var maxHeight = 0
        var left = leftPadding
        var top = paddingTop
        var layoutWidth = measuredWidth - paddingRight
        var localRowCount = 1

        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child.visibility != GONE) {
                val childLp = child.layoutParams as MarginLayoutParams
                val childWidth = child.measuredWidth
                val childHeight = child.measuredHeight
                left += childLp.leftMargin

                maxHeight = max(childHeight + childLp.bottomMargin + childLp.topMargin, maxHeight)
                if (childWidth + left >= layoutWidth) {
                    localRowCount += 1
                    if (localRowCount > maxRowCount) break
                    left = leftPadding + childLp.leftMargin
                    top += maxHeight
                    child.layout(left, top + childLp.topMargin, left + childWidth, top + childLp.topMargin + childHeight)
                    left += childWidth + childLp.rightMargin
                } else {
                    child.layout(left, top + childLp.topMargin, left + childWidth, top + childLp.topMargin + childHeight )
                    left += childWidth + childLp.rightMargin
                }
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var maxWidth = 0
        var layoutWidth = screenWidth - marginLeft - marginRight
        var maxHeight = 0
        var temporaryAccumulatedWidth = 0
        var temporaryAccumulatedHeight = 0
        var localRowCount = 1
        var childState = 0

        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child.visibility != GONE) {
                measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0)
                val childLayoutParams = child.layoutParams as MarginLayoutParams
                val childWidth =
                    child.measuredWidth + childLayoutParams.leftMargin + childLayoutParams.rightMargin
                val childHeight =
                    child.measuredHeight + childLayoutParams.topMargin + childLayoutParams.bottomMargin

                if (temporaryAccumulatedWidth + childWidth >= layoutWidth) {
                    temporaryAccumulatedWidth = 0
                    localRowCount += 1
                    if (localRowCount > maxRowCount) break
                }
                temporaryAccumulatedWidth += childWidth
                temporaryAccumulatedHeight =
                    max(temporaryAccumulatedHeight, childHeight)

                maxWidth = max(temporaryAccumulatedWidth, maxWidth) + paddingLeft + paddingRight
                maxHeight = temporaryAccumulatedHeight * localRowCount + paddingTop + paddingBottom
                childState = combineMeasuredStates(childState, child.measuredState)
            }
        }

        setMeasuredDimension(
            resolveSizeAndState(maxWidth, widthMeasureSpec, childState),
            resolveSizeAndState(maxHeight, heightMeasureSpec, childState)
        )
    }

    override fun checkLayoutParams(p: LayoutParams?): Boolean {
        return p is MarginLayoutParams
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(context, attrs!!)
    }
}