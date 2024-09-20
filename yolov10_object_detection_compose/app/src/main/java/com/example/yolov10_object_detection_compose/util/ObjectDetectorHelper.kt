package com.example.yolov10_object_detection_compose.util

import android.app.Application
import android.graphics.Bitmap
import android.os.SystemClock
import com.example.yolov10_object_detection_compose.R
import com.example.yolov10_object_detection_compose.model.BoundingBox
import com.example.yolov10_object_detection_compose.model.Detections
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.gpu.GpuDelegate
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.ops.CastOp
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer

class ObjectDetectorHelper(
    private val application: Application,
    private val detectionListener: DetectionListener
) {

    private var interpreter: Interpreter? = null
    private var labels = mutableListOf<String>()
    private var tensorWidth = 0
    private var tensorHeight = 0
    private var numChannel = 0
    private var numElements = 0

    init {
        createInterpreter()
    }

    private fun createInterpreter() {
        if (interpreter != null) return

        val options = Interpreter.Options()
        val modelFile = FileUtil.loadMappedFile(application.applicationContext, MODEL_FILE)
        interpreter =
            Interpreter(modelFile, options)

        val compatibilityList = CompatibilityList()
        if (compatibilityList.isDelegateSupportedOnThisDevice) {
            options.addDelegate(GpuDelegate(compatibilityList.bestOptionsForThisDevice))
        } else {
            options.setNumThreads(NUM_THREAD)
        }

        labels.addAll(FileLoaderUtil.loadLabelsFromFile(R.raw.labels, application))

        val inputShape = interpreter!!.getInputTensor(0).shape()
        val outputShape = interpreter!!.getOutputTensor(0).shape()

        if (inputShape.size >= 3) {
            tensorWidth = inputShape[1]
            tensorHeight = inputShape[2]
        }

        if (outputShape.size >= 3) {
            numElements = outputShape[1]
            numChannel = outputShape[2]
        }
    }

    fun detectObject(inputBitmap: Bitmap) {
        if (tensorWidth == 0
            || tensorHeight == 0
            || numChannel == 0
            || numElements == 0
        ) return



        var inferenceTime = SystemClock.uptimeMillis()

        val resizedBitmap = Bitmap.createScaledBitmap(inputBitmap, tensorWidth, tensorHeight, false)
        val tensorImage = TensorImage(INPUT_IMAGE_TYPE).also { it.load(resizedBitmap) }

        val imageProcessor = ImageProcessor.Builder()
            .add(NormalizeOp(INPUT_MEAN, INPUT_STANDARD_DEVIATION))
            .add(CastOp(INPUT_IMAGE_TYPE))
            .build()

        val processedImage = imageProcessor.process(tensorImage)
        val output = TensorBuffer.createFixedSize(
            intArrayOf(1, numChannel, numElements),
            OUTPUT_IMAGE_TYPE
        )
        interpreter!!.run(processedImage.buffer, output.buffer)

        val processedOutput = processOutput(output.floatArray)

        inferenceTime = SystemClock.uptimeMillis() - inferenceTime

        detectionListener.onDetect(detections = processedOutput, inferenceTime = inferenceTime)


    }

    private fun processOutput(array: FloatArray): List<Detections> {
        val detections = mutableListOf<Detections>()
        for (r in 0 until numElements) {
            val cnf = array[r * numChannel + 4]
            if (cnf > CONFIDENCE_THRESHOLD) {
                val x1 = array[r * numChannel]
                val y1 = array[r * numChannel + 1]
                val x2 = array[r * numChannel + 2]
                val y2 = array[r * numChannel + 3]
                val cls = array[r * numChannel + 5].toInt()
                val clsName = if(cls >= labels.size) continue else labels[cls]
                detections.add(
                    Detections(
                        displayName = clsName,
                        score = cnf,
                        boundingBox = BoundingBox(x1, y1, x2, y2)
                    )
                )
            }
        }
        return detections
    }

    interface DetectionListener {
        fun onDetect(detections: List<Detections>, inferenceTime: Long)
    }

    companion object {
        private const val NUM_THREAD = 2
        private const val MODEL_FILE = "yolov10n_float16.tflite"
        private const val INPUT_MEAN = 0f
        private const val INPUT_STANDARD_DEVIATION = 255f
        private val INPUT_IMAGE_TYPE = DataType.FLOAT32
        private val OUTPUT_IMAGE_TYPE = DataType.FLOAT32
        private const val CONFIDENCE_THRESHOLD = 0.6F
    }

}