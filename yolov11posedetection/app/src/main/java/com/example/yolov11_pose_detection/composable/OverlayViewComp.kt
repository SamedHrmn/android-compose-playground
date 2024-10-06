package com.example.yolov11_pose_detection.composable

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.yolov11_pose_detection.data.BoundingBox

@Composable
fun OverlayViewComposable(results: List<BoundingBox>) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            OverlayView(context, null)
        },
        update = { view ->
            view.setResults(results)
        }
    )
}

class OverlayView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var results = listOf<BoundingBox>()
    private var boxPaint = Paint()
    private var textBackgroundPaint = Paint()
    private var textPaint = Paint()

    init {
        initPaints()
    }

    private fun initPaints() {
        textBackgroundPaint.color = Color.WHITE
        textBackgroundPaint.style = Paint.Style.FILL
        textBackgroundPaint.textSize = 50f

        textPaint.color = Color.WHITE
        textPaint.style = Paint.Style.FILL
        textPaint.textSize = 50f

        boxPaint.color = Color.WHITE
        boxPaint.strokeWidth = 10F
        boxPaint.style = Paint.Style.STROKE
    }

    private val radius = 10.0f
    private val keypointPaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 10f
    }

    private val edges = listOf(
        Pair(0, 1), Pair(0, 2), // Nose ↔ Left Eye, Nose ↔ Right Eye
        Pair(1, 3), Pair(2, 4), // Left Eye ↔ Left Ear, Right Eye ↔ Right Ear
        Pair(0, 5), Pair(0, 6), // Nose ↔ Left Shoulder, Nose ↔ Right Shoulder
        Pair(5, 7), Pair(7, 9), // Left Shoulder ↔ Left Elbow, Left Elbow ↔ Left Wrist
        Pair(6, 8), Pair(8, 10), // Right Shoulder ↔ Right Elbow, Right Elbow ↔ Right Wrist
        Pair(5, 6), // Left Shoulder ↔ Right Shoulder
        Pair(5, 11), Pair(6, 12), // Left Shoulder ↔ Left Hip, Right Shoulder ↔ Right Hip
        Pair(11, 12), // Left Hip ↔ Right Hip
        Pair(11, 13), Pair(13, 15), // Left Hip ↔ Left Knee, Left Knee ↔ Left Ankle
        Pair(12, 14), Pair(14, 16) // Right Hip ↔ Right Knee, Right Knee ↔ Right Ankle
    )

    override fun draw(canvas: Canvas) {
        super.draw(canvas)


        val scaledKeyPoints = results.map { boundingBox ->
            boundingBox.keyPoints.map { keypoint ->
                Pair(keypoint.x * width, keypoint.y * height)
            }
        }

        scaledKeyPoints.forEach { boundingBox ->
            boundingBox.forEach { (keypointX, keypointY) ->
                canvas.drawCircle(keypointX, keypointY, radius, keypointPaint)
            }
        }

        if (results.isNotEmpty()) {
            val boundingBox = scaledKeyPoints[0]
            edges.forEach { edge ->
                val (startX, startY) = boundingBox[edge.first]
                val (endX, endY) = boundingBox[edge.second]
                canvas.drawLine(startX, startY, endX, endY, boxPaint)
            }
        }
    }

    fun setResults(boundingBoxes: List<BoundingBox>) {
        results = boundingBoxes
        invalidate()
    }
}