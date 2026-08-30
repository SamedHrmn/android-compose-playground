package com.example.baglamaanalyzer.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.baglamaanalyzer.dsp.PitchResult
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun BaglamaOverlay(
    pitchResult: PitchResult,
    modifier: Modifier = Modifier
) {

    val animatedHz by animateFloatAsState(
        targetValue = pitchResult.frequency,
        animationSpec = tween(durationMillis = 350),
        label = "HzSmoothing"
    )

    Box(modifier = modifier.fillMaxSize()) {
        // --- Makam Indicator ---
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(12.dp))
                .padding(16.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = "Makam: ${pitchResult.makam}",
                color = Color.Cyan,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        // --- Frequency & Large Radial Cent Gauge ---
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp), // Moved down as requested
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RadialCentGauge(frequency = animatedHz)

            Spacer(modifier = Modifier.height(16.dp))

            // Consolidated Display (Note Name + Hz)
            Text(
                text = if (pitchResult.noteName == "...") "WAITING" else pitchResult.noteName,
                color = Color.White,
                fontSize = 38.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Text(
                text = if (animatedHz > 0)
                    String.format(Locale.US, "%.1f Hz", animatedHz)
                else "0.0 Hz",
                color = Color.Green,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun RadialCentGauge(frequency: Float) {
    val targetCents = if (frequency > 0) (frequency.toInt() % 5) - 2 else -2
    val animatedCents by animateFloatAsState(
        targetValue = targetCents.toFloat(),
        animationSpec = tween(durationMillis = 400),
        label = "NeedleSmoothing"
    )

    Canvas(modifier = Modifier.size(200.dp)) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = size.width / 2f


        drawArc(
            color = Color.DarkGray,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(0f, 0f),
            size = size,
            style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
        )

        drawLine(
            color = Color.White.copy(alpha = 0.5f),
            start = Offset(center.x, center.y - radius + 5.dp.toPx()),
            end = Offset(center.x, center.y - radius + 25.dp.toPx()),
            strokeWidth = 3.dp.toPx()
        )

        val angle = 270f + (animatedCents * 25f)
        val angleRad = Math.toRadians(angle.toDouble())
        val needleLen = radius - 20.dp.toPx()
        val endX = center.x + (needleLen.toDouble() * cos(angleRad)).toFloat()
        val endY = center.y + (needleLen.toDouble() * sin(angleRad)).toFloat()

        drawLine(
            color = if (targetCents == 0 && frequency > 0) Color.Green else Color.Red,
            start = center,
            end = Offset(endX, endY),
            strokeWidth = 6.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}
