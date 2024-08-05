package com.sahu.playground.commonCompose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.progressSemantics
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.math.floor

@Composable
fun StepProgressIndicator(
    items: Int,
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = ProgressIndicatorDefaults.linearColor,
    trackColor: Color = ProgressIndicatorDefaults.linearTrackColor,
    strokeCap: StrokeCap = ProgressIndicatorDefaults.LinearStrokeCap
) {
    val coercedProgress = progress.coerceIn(0f, items.toFloat())
    Canvas(
        modifier
            .progressSemantics(coercedProgress)
            .height(height = 4.dp)
    ) {
        val strokeWidth = size.height

        repeat(items) {
            //Track
            drawLinearIndicator(it.toFloat()/items.toFloat(), (it+1).toFloat()/items.toFloat(), trackColor, strokeWidth, strokeCap)

            //Progress
            if(it < floor(coercedProgress)) {
                val endFraction = ((it+1).toFloat())/items.toFloat()
                drawLinearIndicator(it.toFloat() / items.toFloat(), endFraction, color, strokeWidth, strokeCap)
            } else if(it.toFloat() == floor(coercedProgress)) {
                val endFraction = (coercedProgress)/items.toFloat()
                drawLinearIndicator(it.toFloat() / items.toFloat(), endFraction, color, strokeWidth, strokeCap)
            }
        }
    }
}

private fun DrawScope.drawLinearIndicator(
    startFraction: Float,
    endFraction: Float,
    color: Color,
    strokeWidth: Float,
    strokeCap: StrokeCap,
) {
    val width = size.width
    val height = size.height
    // Start drawing from the vertical center of the stroke
    val yOffset = height / 2

    val isLtr = layoutDirection == LayoutDirection.Ltr
    val barStart = (if (isLtr) startFraction else 1f - endFraction) * width + size.height
    val barEnd = (if (isLtr) endFraction else 1f - startFraction) * width - size.height

    // if there isn't enough space to draw the stroke caps, fall back to StrokeCap.Butt
    if (strokeCap == StrokeCap.Butt || height > width) {
        // Progress line
        drawLine(color, Offset(barStart, yOffset), Offset(barEnd, yOffset), strokeWidth)
    } else {
        // need to adjust barStart and barEnd for the stroke caps
        val strokeCapOffset = strokeWidth / 2
        val coerceRange = strokeCapOffset..(width - strokeCapOffset)
        val adjustedBarStart = barStart.coerceIn(coerceRange)
        val adjustedBarEnd = barEnd.coerceIn(coerceRange)

        if (abs(endFraction - startFraction) > 0) {
            // Progress line
            drawLine(
                color,
                Offset(adjustedBarStart, yOffset),
                Offset(adjustedBarEnd, yOffset),
                strokeWidth,
                strokeCap,
            )
        }
    }
}

@Preview
@Composable
private fun StepProgressIndicatorPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        for(i in 0..30 step 2)
            StepProgressIndicator(modifier = Modifier.fillMaxWidth(), items = 3, progress = i/10f, strokeCap = StrokeCap.Round)
    }
}