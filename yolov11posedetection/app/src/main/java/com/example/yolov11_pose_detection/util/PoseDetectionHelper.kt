package com.example.yolov11_pose_detection.util

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.SystemClock
import com.example.yolov11_pose_detection.data.BoundingBox
import com.example.yolov11_pose_detection.data.KeyPoint
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

class PoseDetectionHelper(
    private val application: Application,
    private val poseDetectionListener: PoseDetectionListener
) {
    private var interpreter: Interpreter? = null

    private var tensorWidth = 0
    private var tensorHeight = 0
    private var numChannel = 0
    private var numElements = 0

    private val imageProcessor = ImageProcessor.Builder()
        .add(NormalizeOp(INPUT_MEAN, INPUT_STANDARD_DEVIATION))
        .add(CastOp(INPUT_IMAGE_TYPE))
        .build()

    init {
        createInterpreter()
    }

    fun close() {
        interpreter?.close()
        interpreter = null
    }

    private fun createInterpreter() {
        if (interpreter != null) {
            close()
        }

        val options = Interpreter.Options()
        val modelFile = FileUtil.loadMappedFile(application.applicationContext, MODEL_FILE)

        val compatibilityList = CompatibilityList()
        if (compatibilityList.isDelegateSupportedOnThisDevice) {
            options.addDelegate(GpuDelegate(compatibilityList.bestOptionsForThisDevice))
        } else {
            options.setNumThreads(NUM_THREAD)
        }

        interpreter =
            Interpreter(modelFile, options)


        val inputShape = interpreter!!.getInputTensor(0).shape()
        val outputShape = interpreter!!.getOutputTensor(0).shape()


        tensorWidth = inputShape[1]
        tensorHeight = inputShape[2]

        if (inputShape[1] == 3) {
            tensorWidth = inputShape[2]
            tensorHeight = inputShape[3]
        }

        numChannel = outputShape[1]
        numElements = outputShape[2]
    }

    fun detectObject(inputBitmap: Bitmap) {
        if (tensorWidth == 0
            || tensorHeight == 0
            || numChannel == 0
            || numElements == 0
        ) return


        var inferenceTime = SystemClock.uptimeMillis()

        val resizedBitmap = createPaddedBitmap(inputBitmap, tensorWidth, tensorHeight)
        val tensorImage = TensorImage(INPUT_IMAGE_TYPE).also { it.load(resizedBitmap) }


        val processedImage = imageProcessor.process(tensorImage)
        val output = TensorBuffer.createFixedSize(
            intArrayOf(1, numChannel, numElements),
            OUTPUT_IMAGE_TYPE
        )
        interpreter!!.run(processedImage.buffer, output.buffer)

        val processedOutput = processOutput(
            output.floatArray,
        )

        if(processedOutput == null){
            poseDetectionListener.onEmptyDetect()
            return
        }

        inferenceTime = SystemClock.uptimeMillis() - inferenceTime
        poseDetectionListener.onDetect(detections = processedOutput, inferenceTime = inferenceTime)
    }

    private fun getScaledDimensions(
        originalWidth: Int,
        originalHeight: Int,
        targetWidth: Int,
        targetHeight: Int
    ): Pair<Int, Int> {
        val aspectRatio = originalWidth.toFloat() / originalHeight
        var newWidth = targetWidth
        var newHeight = (targetWidth / aspectRatio).toInt()

        if (newHeight > targetHeight) {
            newHeight = targetHeight
            newWidth = (targetHeight * aspectRatio).toInt()
        }

        return Pair(newWidth, newHeight)
    }

    private fun createPaddedBitmap(
        originalBitmap: Bitmap,
        targetWidth: Int,
        targetHeight: Int
    ): Bitmap {
        val (newWidth, newHeight) = getScaledDimensions(
            originalBitmap.width,
            originalBitmap.height,
            targetWidth,
            targetHeight
        )

        val paddedBitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(paddedBitmap)
        val paint = Paint().apply { color = Color.BLACK }

        canvas.drawRect(0f, 0f, targetWidth.toFloat(), targetHeight.toFloat(), paint)

        val left = (targetWidth - newWidth) / 2f
        val top = (targetHeight - newHeight) / 2f

        val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)
        canvas.drawBitmap(scaledBitmap, left, top, null)

        return paddedBitmap
    }

    private fun processOutput(
        array: FloatArray,
    ): List<BoundingBox>? {
        val boundingBoxes = mutableListOf<BoundingBox>()

        for (c in 0 until numElements) {
            val cnf = array[c + numElements * 4]
            if (cnf > CONFIDENCE_THRESHOLD) {
                val cx = array[c]
                val cy = array[c + numElements]
                val w = array[c + numElements * 2]
                val h = array[c + numElements * 3]
                val x1 = cx - (w / 2F)
                val y1 = cy - (h / 2F)
                val x2 = cx + (w / 2F)
                val y2 = cy + (h / 2F)
                if (x1 <= 0F || x1 >= tensorWidth) continue
                if (y1 <= 0F || y1 >= tensorHeight) continue
                if (x2 <= 0F || x2 >= tensorWidth) continue
                if (y2 <= 0F || y2 >= tensorHeight) continue

                val keypoints = mutableListOf<KeyPoint>()
                for (k in 0 until 17) {
                    var kx = array[c + numElements * (5 + k * 3)]
                    var ky = array[c + numElements * (5 + k * 3 + 1)]

                    kx /= tensorWidth
                    ky /= tensorHeight

                    keypoints.add(KeyPoint(kx, ky))
                }

                boundingBoxes.add(
                    BoundingBox(
                        x1 = x1, y1 = y1, x2 = x2, y2 = y2,
                        cx = cx, cy = cy, w = w, h = h, cnf = cnf,
                        keyPoints = keypoints
                    )
                )
            }
        }

        if(boundingBoxes.isEmpty()) return null

        return applyNMS(boundingBoxes)
    }

    private fun applyNMS(boxes: List<BoundingBox>): MutableList<BoundingBox> {
        val sortedBoxes = boxes.sortedByDescending { it.cnf }.toMutableList()
        val selectedBoxes = mutableListOf<BoundingBox>()

        while (sortedBoxes.isNotEmpty()) {
            val first = sortedBoxes.first()
            selectedBoxes.add(first)
            sortedBoxes.remove(first)

            val iterator = sortedBoxes.iterator()
            while (iterator.hasNext()) {
                val nextBox = iterator.next()
                val iou = calculateIoU(first, nextBox)
                if (iou >= IOU_THRESHOLD) {
                    iterator.remove()
                }
            }
        }

        return selectedBoxes
    }

    private fun calculateIoU(box1: BoundingBox, box2: BoundingBox): Float {
        val x1 = maxOf(box1.x1, box2.x1)
        val y1 = maxOf(box1.y1, box2.y1)
        val x2 = minOf(box1.x2, box2.x2)
        val y2 = minOf(box1.y2, box2.y2)
        val intersectionArea = maxOf(0F, x2 - x1) * maxOf(0F, y2 - y1)
        val box1Area = box1.w * box1.h
        val box2Area = box2.w * box2.h
        return intersectionArea / (box1Area + box2Area - intersectionArea)
    }


    interface PoseDetectionListener {
        fun onEmptyDetect()
        fun onDetect(detections: List<BoundingBox>, inferenceTime: Long)
    }

    companion object {
        private const val NUM_THREAD = 4
        const val MODEL_FILE = "yolo11n-pose_float16.tflite"
        private const val INPUT_MEAN = 0f
        private const val INPUT_STANDARD_DEVIATION = 255f
        private val INPUT_IMAGE_TYPE = DataType.FLOAT32
        private val OUTPUT_IMAGE_TYPE = DataType.FLOAT32
        private const val CONFIDENCE_THRESHOLD = 0.3f
        private const val IOU_THRESHOLD = 0.5F
    }
}