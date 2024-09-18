package com.example.seminoma_analyzer.data

import android.graphics.PointF

data class KeyPoint(val bodyPart: BodyPart, var coordinate: PointF, val score: Float)
