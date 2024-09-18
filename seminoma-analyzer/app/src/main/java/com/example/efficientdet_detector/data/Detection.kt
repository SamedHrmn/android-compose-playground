package com.example.seminoma_analyzer.data

import android.graphics.RectF

data class Detection(
    val boundingBox: RectF,
    val detectedObjectName: String,
    val confidenceScore: Float,
    val tensorImageHeight: Int,
    val tensorImageWidth: Int,
    val inferenceTime:String,
)