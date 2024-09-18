package com.example.seminoma_analyzer.ml

import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import com.example.seminoma_analyzer.data.Detection
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.TensorImage

abstract class EfficientdetWrapper<T,O>(private val context: Context) : Efficientdet.IDetector {

    abstract fun createModelInstance(context: Context): T
    abstract fun processModel(model: T, tensorImage: TensorImage): O
    abstract fun getDetectionResult(outputs: O,tensorImage: TensorImage,inferenceTime: Long? = null): Detection

    abstract fun closeModel(model: T)

    override fun process(frame: Bitmap, tensorWidth: Int, tensorHeight: Int): Detection {
        val inferenceTime = SystemClock.uptimeMillis()

        val model = createModelInstance(context)
        val resizedBitmap = Bitmap.createScaledBitmap(frame, tensorWidth, tensorHeight, false)
        val tensorImage = TensorImage(DataType.UINT8)
        tensorImage.load(resizedBitmap)

        val outputs = processModel(model, tensorImage)
        val detectionResult = getDetectionResult(outputs,tensorImage)

        closeModel(model)

        val totalTime = SystemClock.uptimeMillis() - inferenceTime
        return detectionResult.copy(inferenceTime = totalTime.toInt().toString())
    }
}

