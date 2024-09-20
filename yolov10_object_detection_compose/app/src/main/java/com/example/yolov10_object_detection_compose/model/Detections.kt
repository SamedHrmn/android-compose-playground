package com.example.yolov10_object_detection_compose.model

data class Detections(val displayName:String,val score:Float,val boundingBox:BoundingBox)

data class BoundingBox(val x1:Float,val y1:Float,val x2:Float,val y2:Float)