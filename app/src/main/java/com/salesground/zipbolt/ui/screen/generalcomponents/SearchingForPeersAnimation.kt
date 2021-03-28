package com.salesground.zipbolt.ui.screen.generalcomponents

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.salesground.zipbolt.R

@Composable
fun SearchingForPeersAnimation(circlePeekRadius : Float,
circleBaseRadius: Float, numberOfCircle : Int, color: Color){

    val infiniteTransition
    val animatedRadius
    Box(contentAlignment = Alignment.Center) {
        // draw circles
        Canvas(modifier = Modifier.wrapContentSize()) {

            drawCircle(brush = Brush.linearGradient(listOf(color.copy(alpha = 0.5f),
                color.copy(alpha = 0.3f), color.copy(alpha = 0.2f),
                color.copy(alpha = 0.05f))), radius = circleBaseRadius)

            drawCircle(brush = Brush.linearGradient(listOf(color.copy(alpha = 0.5f),
                color.copy(alpha = 0.3f), color.copy(alpha = 0.2f),
                color.copy(alpha = 0.05f))), radius = circleBaseRadius)
        }

        // draw Image
        Image(painter = painterResource(R.drawable.android_icon), contentDescription =  "",
        modifier = Modifier.requiredSize((circleBaseRadius * LocalContext.current.resources.displayMetrics.scaledDensity).dp))
    }
}