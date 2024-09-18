package com.example.seminoma_analyzer

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.seminoma_analyzer.data.Detection
import com.example.seminoma_analyzer.ml.Efficientdet
import com.example.seminoma_analyzer.ml.ModelType
import kotlinx.coroutines.coroutineScope


class MainActivityViewModel(application: Application) : ViewModel() {

    private val modelType = ModelType.Lite4
    private val efficientdet = Efficientdet(application.applicationContext, modelType)

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

    suspend fun createObjectDetector() {
        coroutineScope {
            efficientdet.create()
        }
    }

    fun detectObject(bitmap: Bitmap): Detection {
        return efficientdet.detectObject(bitmap)
    }


}