package com.example.yolov11_pose_detection.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.yolov11_pose_detection.data.BoundingBox
import com.example.yolov11_pose_detection.util.PoseDetectionHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CameraPreviewViewModel(application: Application) : ViewModel(),
    PoseDetectionHelper.PoseDetectionListener {

    private val poseDetectionHelper = PoseDetectionHelper(application, this)

    private val _results = MutableStateFlow<List<BoundingBox>>(emptyList())
    val detections: StateFlow<List<BoundingBox>> = _results

    private val _inferenceTime = MutableStateFlow(0L)
    val inferenceTime: StateFlow<Long> = _inferenceTime

    companion object {
        @Suppress("UNCHECKED_CAST")
        class Factory(private val application: Application) : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(CameraPreviewViewModel::class.java)) {
                    return CameraPreviewViewModel(application) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }

    override fun onCleared() {
        poseDetectionHelper.close()
        super.onCleared()
    }

    override fun onEmptyDetect() {
        _results.value = emptyList()
    }

    override fun onDetect(detections: List<BoundingBox>, inferenceTime: Long) {
        _results.value = detections
        _inferenceTime.value = inferenceTime
    }

    fun detect(input: Bitmap) {
        poseDetectionHelper.detectObject(input)
    }

}