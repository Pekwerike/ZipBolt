package com.salesground.zipbolt.ui.animationutils

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.graphics.drawable.ColorDrawable
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb


fun ImageView.scaleUpAnimation(parent:FrameLayout){
    val scaleUpAnimation = ValueAnimator.ofFloat(0.7f, 1f).apply {
        duration = 350
        interpolator = AccelerateDecelerateInterpolator()
    }
    scaleUpAnimation.addUpdateListener {
        val currentScale = it.animatedValue as Float
        scaleX = currentScale
        scaleY = currentScale
    }
    val viewGroupColorChangeAnimation =
        ValueAnimator.ofArgb(Color.Blue.copy(0.12f).toArgb(), Color.Transparent.toArgb()).apply {
            duration = 350
        }
    viewGroupColorChangeAnimation.addUpdateListener {
        parent.foreground = ColorDrawable(it.animatedValue as Int)
    }

    AnimatorSet().apply {
        playTogether(scaleUpAnimation, viewGroupColorChangeAnimation)
        start()
    }

}
fun ImageView.scaleDownAnimation(parent: FrameLayout) {
    val scaleDownAnimation = ValueAnimator.ofFloat(1f, 0.7f).apply {
        duration = 350
        interpolator = AccelerateDecelerateInterpolator()
    }
    scaleDownAnimation.addUpdateListener {
        val currentScale = it.animatedValue as Float
        scaleX = currentScale
        scaleY = currentScale
    }
    val viewGroupColorChangeAnimation =
        ValueAnimator.ofArgb(Color.Transparent.toArgb(), Color.Blue.copy(0.12f).toArgb()).apply {
            duration = 350
        }
    viewGroupColorChangeAnimation.addUpdateListener {
        parent.foreground = ColorDrawable(it.animatedValue as Int)
    }
    AnimatorSet().apply {
        playTogether(scaleDownAnimation, viewGroupColorChangeAnimation)
        start()
    }
}
