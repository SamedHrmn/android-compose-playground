package com.example.efficientdet_detector.ml

import android.content.Context
import android.graphics.Bitmap
import com.example.efficientdet_detector.data.Detection
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.gpu.GpuDelegate
import org.tensorflow.lite.support.common.FileUtil

enum class ModelType(val modelPath: String,val modelName:String) {
    Lite1("efficientdet-lite1.tflite","EfficientDet-Lite1"),
    Lite2("efficientdet-lite2.tflite","EfficientDet-Lite2"),
    Lite4("efficientdet-lite4.tflite","EfficientDet-Lite4")
}

class Efficientdet(private val context: Context, private val modelType: ModelType) {

    private var interpreter: Interpreter? = null

    private var tensorWidth = 0
    private var tensorHeight = 0

    fun modelShouldRestart(){
        interpreter = null
        create()
    }

    fun create() {
        if (interpreter != null) return

        try {
            val options = Interpreter.Options()

            val compatibilityList = CompatibilityList()
            if (compatibilityList.isDelegateSupportedOnThisDevice) {
                val gpuOptions = compatibilityList.bestOptionsForThisDevice
                options.addDelegate(GpuDelegate(gpuOptions))
            } else {
                options.setNumThreads(2)
            }

            interpreter = Interpreter(
                FileUtil.loadMappedFile(context, modelType.modelPath),
                options
            )

            val inputShape = interpreter!!.getInputTensor(0).shape()

            if (inputShape != null) {
                tensorWidth = inputShape[1]
                tensorHeight = inputShape[2]


                if (inputShape[1] == 3) {
                    tensorWidth = inputShape[2]
                    tensorHeight = inputShape[3]
                }
            }

        } catch (e: Exception) {
            print(e.stackTrace)
        }
    }

    fun detectObject(cameraFrame: Bitmap): Detection? {
        val wrapper: EfficientdetWrapper<*, *> = when (modelType) {
            ModelType.Lite1 -> EfficientdetLite1Wrapper(context)
            ModelType.Lite2 -> EfficientdetLite2Wrapper(context)
            ModelType.Lite4 -> EfficientdetLite4Wrapper(context)
        }
        return wrapper.process(
            frame = cameraFrame,
            tensorWidth = tensorWidth,
            tensorHeight = tensorHeight
        )
    }


    interface IDetector {
        fun process(frame: Bitmap, tensorWidth: Int, tensorHeight: Int): Detection?
    }

}

