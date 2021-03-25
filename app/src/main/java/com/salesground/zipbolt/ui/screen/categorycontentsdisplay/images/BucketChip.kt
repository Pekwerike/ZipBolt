package com.salesground.zipbolt.ui.screen.categorycontentsdisplay.images

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp


@Composable
fun BucketChip(
    bucketName: String = "All",
    isChosen: Boolean = false, onClick: (String) -> Unit
) {
    val editedBucketName = remember (bucketName){
        if(bucketName.length > 9){
            "${bucketName.take(9)}..."
        }else {
            bucketName
        }
    }
    Box(
        modifier = Modifier.padding(horizontal = 3.dp, vertical = 2.dp)
            .clip(shape = MaterialTheme.shapes.small.copy(CornerSize(5.dp)))
            .clickable {
                onClick(bucketName)
            },
        contentAlignment = Alignment.Center
    ) {
        if (isChosen) {
            Surface(color = MaterialTheme.colors.primary.copy(alpha = 0.3f)) {
                Text(
                    modifier = Modifier.padding(horizontal = 10.dp, 5.dp)
                        .animateContentSize(),
                    text = editedBucketName, color = MaterialTheme.colors.primary,
                    style = MaterialTheme.typography.body2,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold
                )
            }
        } else {
            Surface(color = Color.LightGray.copy(0.5f)) {
                Text(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    text = editedBucketName, color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.caption,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}