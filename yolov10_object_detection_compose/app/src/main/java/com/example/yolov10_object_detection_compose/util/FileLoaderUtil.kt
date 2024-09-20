package com.example.yolov10_object_detection_compose.util

import android.content.Context
import java.io.BufferedReader

object FileLoaderUtil {
    fun loadLabelsFromFile(resourceId:Int,context: Context): List<String> {
        val fileInputStream = context.resources.openRawResource(resourceId)
        val labels = mutableListOf<String>()
        BufferedReader(fileInputStream.reader()).useLines {
            labels.addAll(it)
        }
        return labels
    }
}