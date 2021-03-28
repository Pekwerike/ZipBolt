package com.salesground.zipbolt.ui.screen.generalcomponents

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import kotlin.math.max

@Composable
fun SearchingForPeersAnimation(
    circlePeekRadius: Float,
    circleBaseRadius: Float = circlePeekRadius * 0.2f,
    baseColor: Color,
    peekColor: Color,
    duration: Int = 1000
) {

    val infiniteTransition = rememberInfiniteTransition()
    val animatedRadius by infiniteTransition.animateFloat(
        initialValue = circleBaseRadius,
        targetValue = circlePeekRadius,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = duration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    val color by infiniteTransition.animateColor(
        initialValue = baseColor,
        targetValue = peekColor,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = duration / 2, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(contentAlignment = Alignment.Center) {
        // draw circles
        Canvas(modifier = Modifier.requiredSize(circlePeekRadius.dp)) {

            drawCircle(
                brush = Brush.radialGradient(
                    listOf(
                        color.copy(alpha = 0.5f),
                        color.copy(alpha = 0.3f), color.copy(alpha = 0.2f),
                        color.copy(alpha = 0.05f)
                    )
                ), radius = circleBaseRadius
            )

            drawCircle(
                brush = Brush.horizontalGradient(
                    listOf(
                        color.copy(alpha = 0.5f),
                        color.copy(alpha = 0.3f), color.copy(alpha = 0.2f),
                        color.copy(alpha = 0.05f)
                    )
                ), radius = max(animatedRadius * 0.3f, circleBaseRadius)
            )

            drawCircle(
                brush = Brush.verticalGradient(
                    listOf(
                        color.copy(alpha = 0.5f),
                        color.copy(alpha = 0.3f), color.copy(alpha = 0.2f),
                        color.copy(alpha = 0.05f)
                    )
                ), radius = max(
                    animatedRadius * 0.6f,
                    circleBaseRadius
                )
            )


            drawCircle(
                brush = Brush.linearGradient(
                    listOf(
                        color.copy(alpha = 0.5f),
                        color.copy(alpha = 0.3f), color.copy(alpha = 0.2f),
                        color.copy(alpha = 0.05f)
                    )
                ), radius = animatedRadius
            )

        }
        Icon(
            imageVector = Icons.Rounded.Person, contentDescription = "",
            modifier = Modifier
                .requiredSize((circleBaseRadius * 0.55).dp)
        )
    }
}