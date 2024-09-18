package com.example.efficientdet_detector.ui.common

import android.graphics.Paint
import android.graphics.RectF
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import com.example.efficientdet_detector.data.Detection

@Composable
fun DrawBoundingBox(detection: Detection, modifier: Modifier = Modifier) {

    val screenWidth = LocalContext.current.resources.displayMetrics.widthPixels * 1f
    val screenHeight = LocalContext.current.resources.displayMetrics.heightPixels * 1f

    val paint = rememberUpdatedState(Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 8f

    })

    val xScale = screenWidth / detection.tensorImageWidth
    val yScale = screenHeight / detection.tensorImageHeight

    // Scale the bounding box from the detection to match the display dimensions
    val scaledBox = RectF(
        detection.boundingBox.left * xScale,
        detection.boundingBox.top * yScale,
        detection.boundingBox.right * xScale,
        detection.boundingBox.bottom * yScale
    ).also {
        // Ensure the bounding box doesn't go outside of the screen dimensions
        it.left = it.left.coerceAtLeast(0f)
        it.top = it.top.coerceAtLeast(0f)
        it.right = it.right.coerceAtMost(screenWidth)
        it.bottom = it.bottom.coerceAtMost(screenHeight)
    }

    val density = LocalDensity.current.density
    val desiredTextSizeInSp = 20
    val pixelSize = desiredTextSizeInSp * density

    Canvas(modifier = modifier) {


        // Draw the bounding box
        drawRect(
            color = Color(paint.value.color),
            size = Size(scaledBox.width(), scaledBox.height()),
            topLeft = Offset(scaledBox.left, scaledBox.top),
            style = Stroke(paint.value.strokeWidth)
        )

        val text =
            "${detection.detectedObjectName} ${(detection.confidenceScore * 100).toInt()}%"
        drawIntoCanvas { canvas ->
            canvas.nativeCanvas.drawText(
                text,
                scaledBox.left,
                scaledBox.top - 10,
                Paint().apply {

                    textSize = pixelSize
                }
            )
        }
    }
}