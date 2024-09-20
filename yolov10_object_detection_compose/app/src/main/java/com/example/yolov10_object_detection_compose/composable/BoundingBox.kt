package com.example.yolov10_object_detection_compose.composable
import android.content.Context
import android.graphics.Paint
import android.graphics.Rect
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yolov10_object_detection_compose.model.Detections
import kotlin.random.Random
import android.graphics.Color as AndroidColor

private fun generateRandomColor(): Pair<Color, Int> {
    val red = Random.nextInt(256)
    val green = Random.nextInt(256)
    val blue = Random.nextInt(256)

    val color = Color(red, green, blue)


    val colorInt = AndroidColor.rgb(red, green, blue)
    return Pair(color, colorInt)
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BoundingBox(detections: List<Detections>?, context: Context?) {
    if (detections == null || context == null) return

    val screenWidth = context.resources.displayMetrics.widthPixels
    val screenHeight = context.resources.displayMetrics.heightPixels

    val colorMap = remember {
        mutableMapOf<String, Pair<Color, Int>>()
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        detections.forEach {
            val box = it.boundingBox
            val text = it.displayName
            val (rectColor, _) = colorMap.getOrPut(it.displayName) {
                generateRandomColor()
            }


            val left = box.x1 * screenWidth
            val top = box.y1 * screenHeight
            val right = box.x2 * screenWidth
            val bottom = box.y2 * screenHeight


            drawRoundRect(
                color = rectColor,
                cornerRadius = CornerRadius(24f, 24f),
                topLeft = Offset(left, top),
                size = Size(right - left, bottom - top),
                style = Stroke(width = 3.dp.toPx())
            )

            drawContext.canvas.nativeCanvas.apply {
                val padding = 8.dp.toPx()

                val textPaint = Paint().apply {
                    color = AndroidColor.WHITE
                    textSize = 24.sp.toPx()
                }

                val textBounds = Rect()


                textPaint.getTextBounds(text, 0, text.length, textBounds)


                drawRoundRect(
                    color = rectColor,
                    topLeft = Offset(left, top),
                    size = Size(
                        textBounds.width() + padding,
                        textBounds.height() + padding
                    ),
                    cornerRadius = CornerRadius(
                        x = 24f,
                        y = 24f
                    )
                )


                drawText(text, left, top + textBounds.height(), textPaint)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
@Preview(showSystemUi = true, showBackground = true)
fun PrevBoundingBox() {
    BoundingBox(
        detections = listOf(
            Detections(
                displayName = "Chair", score = 0.8f,
                com.example.yolov10_object_detection_compose.model.BoundingBox(
                    0.8f,
                    0.46407104f,
                    0.3216002f,
                    0.1858883f,
                )
            )
        ), context = LocalContext.current
    )
}