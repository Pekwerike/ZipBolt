package com.salesground.zipbolt.ui.animationutils

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.graphics.drawable.ColorDrawable
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb


fun ImageView.scaleUpAnimation(
    parent: FrameLayout,
    currentScaleValue: Float = 0.7f,
    currentParentForegroundColor: Int = Color.Blue.copy(0.12f).toArgb(),
    animationDuration: Long = 350
) {
    val scaleUpAnimation = ValueAnimator.ofFloat(currentScaleValue, 1f).apply {
        duration = animationDuration
        interpolator = AccelerateDecelerateInterpolator()
    }
    scaleUpAnimation.addUpdateListener {
        val currentScale = it.animatedValue as Float
        scaleX = currentScale
        scaleY = currentScale
    }
    val viewGroupColorChangeAnimation =
        ValueAnimator.ofArgb(currentParentForegroundColor, Color.Transparent.toArgb()).apply {
            duration = animationDuration
        }
    viewGroupColorChangeAnimation.addUpdateListener {
        parent.foreground = ColorDrawable(it.animatedValue as Int)
    }

    AnimatorSet().apply {
        playTogether(scaleUpAnimation, viewGroupColorChangeAnimation)
        start()
    }

}

fun ImageView.scaleDownAnimation(
    parent: FrameLayout,
    targetScaleValue: Float = 0.7f,
    targetParentForegroundValue: Int = Color.Blue.copy(0.12f).toArgb(),
    animationDuration: Long = 350
) {
    val scaleDownAnimation = ValueAnimator.ofFloat(1f, targetScaleValue).apply {
        duration = animationDuration
        interpolator = AccelerateDecelerateInterpolator()
    }
    scaleDownAnimation.addUpdateListener {
        val currentScale = it.animatedValue as Float
        scaleX = currentScale
        scaleY = currentScale
    }
    val viewGroupColorChangeAnimation =
        ValueAnimator.ofArgb(Color.Transparent.toArgb(), targetParentForegroundValue).apply {
            duration = animationDuration
        }
    viewGroupColorChangeAnimation.addUpdateListener {
        parent.foreground = ColorDrawable(it.animatedValue as Int)
    }
    AnimatorSet().apply {
        playTogether(scaleDownAnimation, viewGroupColorChangeAnimation)
        start()
    }
}
