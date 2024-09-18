package com.example.seminoma_analyzer.ml

import android.content.Context
import com.example.seminoma_analyzer.data.Detection
import org.tensorflow.lite.support.image.TensorImage

class EfficientdetLite2Wrapper(context: Context) : EfficientdetWrapper<EfficientdetLite2,EfficientdetLite2.Outputs>(context) {
  override fun createModelInstance(context: Context) = EfficientdetLite2.newInstance(context)
  override fun getDetectionResult(
    outputs: EfficientdetLite2.Outputs,
    tensorImage: TensorImage,
    inferenceTime: Long?
  ): Detection {
    val detectionResult = outputs.detectionResultList[0]

    val location = detectionResult.locationAsRectF
    val category = detectionResult.categoryAsString
    val score = detectionResult.scoreAsFloat

    return Detection(location, category, score, tensorImage.height, tensorImage.width,inferenceTime?.toInt().toString())
  }


  override fun closeModel(model: EfficientdetLite2) {
    model.close()
  }

  override fun processModel(model: EfficientdetLite2, tensorImage: TensorImage): EfficientdetLite2.Outputs {
    return model.process(tensorImage)
  }

}