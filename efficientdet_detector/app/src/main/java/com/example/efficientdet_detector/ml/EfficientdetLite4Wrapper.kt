package com.example.efficientdet_detector.ml

import android.content.Context
import com.example.efficientdet_detector.data.Detection
import org.tensorflow.lite.support.image.TensorImage

class EfficientdetLite4Wrapper(context: Context) : EfficientdetWrapper<EfficientdetLite4,EfficientdetLite4.Outputs>(context) {
    override fun createModelInstance(context: Context) = EfficientdetLite4.newInstance(context)
    override fun getDetectionResult(
        outputs: EfficientdetLite4.Outputs,
        tensorImage: TensorImage,
        inferenceTime: Long?
    ): Detection {
        val detectionResult = outputs.detectionResultList[0]

        val location = detectionResult.locationAsRectF
        val category = detectionResult.categoryAsString
        val score = detectionResult.scoreAsFloat

        return Detection(location, category, score, tensorImage.height, tensorImage.width,inferenceTime?.toInt().toString())
    }


    override fun closeModel(model: EfficientdetLite4) {
        model.close()
    }

    override fun processModel(model: EfficientdetLite4, tensorImage: TensorImage): EfficientdetLite4.Outputs {
        return model.process(tensorImage)
    }

}