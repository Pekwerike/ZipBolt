package com.salesground.zipbolt.ui.screen.allmediadisplay.categorycontentsdisplay.images.recyclerview

import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import kotlin.math.min

@Composable
fun BucketChipGroup(modifier: Modifier, rowCount: Int = 2, content: @Composable () -> Unit) {

    Layout(modifier = modifier.wrapContentHeight(), content = content) { measureables, constraints ->
        var layoutHeight = 0
        var rowsConfirmed = 1
        var accumulatedWidth = 0
        val layoutWidth = constraints.maxWidth

        val placeables = measureables.map {
            val measure = it.measure(constraints.copy(minHeight = 0))
            accumulatedWidth += measure.width
            if(accumulatedWidth >= layoutWidth) {
                rowsConfirmed +=1
                accumulatedWidth = 0
            }
            layoutHeight = kotlin.math.max(layoutHeight, measure.height)
            measure
        }

        layout(layoutWidth, layoutHeight * min(rowsConfirmed, rowCount)) {
            var x = 0
            var y = 0
            var rowsDrawn = 1

            for(placeable in placeables){
                if(x + placeable.width >= layoutWidth && rowsDrawn < rowCount){
                    x = 0
                    y += placeable.height
                    rowsDrawn += 1
                }else if(rowsDrawn == rowCount && x + placeable.width >= layoutWidth){
                    break
                }
                placeable.placeRelative(x, y)
                x += placeable.width
            }
        }
    }
}