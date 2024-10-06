package com.example.yolov11_pose_detection.data

data class KeyPoint(val x:Float, val y:Float)

data class BoundingBox(
    val x1: Float,
    val y1: Float,
    val x2: Float,
    val y2: Float,
    val cx: Float,
    val cy: Float,
    val w: Float,
    val h: Float,
    val cnf: Float,
    val keyPoints: List<KeyPoint> = emptyList()
)