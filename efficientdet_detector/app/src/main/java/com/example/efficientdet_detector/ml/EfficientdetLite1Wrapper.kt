package com.example.efficientdet_detector.ml

import android.content.Context
import com.example.efficientdet_detector.data.Detection
import org.tensorflow.lite.support.image.TensorImage

class EfficientdetLite1Wrapper(context: Context) : EfficientdetWrapper<EfficientdetLite1,EfficientdetLite1.Outputs>(context) {
    override fun createModelInstance(context: Context) = EfficientdetLite1.newInstance(context)
    override fun getDetectionResult(
        outputs: EfficientdetLite1.Outputs,
        tensorImage: TensorImage,
        inferenceTime: Long?
    ): Detection {
        val detectionResult = outputs.detectionResultList[0]

        val location = detectionResult.locationAsRectF
        val category = detectionResult.categoryAsString
        val score = detectionResult.scoreAsFloat

        return Detection(location, category, score, tensorImage.height, tensorImage.width,inferenceTime?.toInt().toString())
    }


    override fun closeModel(model: EfficientdetLite1) {
        model.close()
    }

    override fun processModel(model: EfficientdetLite1, tensorImage: TensorImage): EfficientdetLite1.Outputs {
        return model.process(tensorImage)
    }

}