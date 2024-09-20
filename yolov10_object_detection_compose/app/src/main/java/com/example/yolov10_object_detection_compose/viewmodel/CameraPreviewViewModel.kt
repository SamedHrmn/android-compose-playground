package com.example.yolov10_object_detection_compose.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.yolov10_object_detection_compose.model.Detections
import com.example.yolov10_object_detection_compose.util.ObjectDetectorHelper

class CameraPreviewViewModel(val application: Application) : ViewModel(),
    ObjectDetectorHelper.DetectionListener {
    private val objectDetectorHelper = ObjectDetectorHelper(application, this)

    var detections = mutableStateListOf<Detections>()
    var inferenceTime = mutableLongStateOf(0L)

    fun detectObject(inputImage: Bitmap) {
        objectDetectorHelper.detectObject(inputImage)
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        class Factory(private val application: Application) : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(CameraPreviewViewModel::class.java)) {
                    return CameraPreviewViewModel(application = application) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }

    override fun onDetect(detections: List<Detections>, inferenceTime: Long) {
        this.detections.clear()
        this.detections.addAll(detections)
        this.inferenceTime.longValue = inferenceTime
    }
}