package com.example.efficientdet_detector

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.efficientdet_detector.data.Detection
import com.example.efficientdet_detector.ml.Efficientdet
import com.example.efficientdet_detector.ml.ModelType
import kotlinx.coroutines.coroutineScope


class MainActivityViewModel(private val application: Application) : ViewModel() {

    private val modelType = ModelType.Lite1
    private var efficientdet = Efficientdet(application.applicationContext, modelType)


    companion object {
        @Suppress("UNCHECKED_CAST")
        class Factory(
            private val application: Application
        ) : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(MainActivityViewModel::class.java)) {
                    return MainActivityViewModel(application) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }

    suspend fun updateSelectedModel(modelType: ModelType){
        efficientdet = Efficientdet(application.applicationContext, modelType)
        coroutineScope {
            efficientdet.modelShouldRestart()
        }
    }

    suspend fun createObjectDetector() {
        coroutineScope {
            efficientdet.create()
        }
    }

    fun detectObject(bitmap: Bitmap): Detection? {
        return efficientdet.detectObject(bitmap)
    }
}
