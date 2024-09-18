package com.example.seminoma_analyzer

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage

import androidx.camera.core.ImageProxy
import java.io.ByteArrayOutputStream


object ImageUtil {
      fun imageToBitmap(image: ImageProxy): Bitmap {
          val yBuffer = image.planes[0].buffer
          val vuBuffer = image.planes[2].buffer

          yBuffer.position(0)
          vuBuffer.position(0)

          val ySize = yBuffer.remaining()
          val vuSize = vuBuffer.remaining()

          val nv21 = ByteArray(ySize + vuSize)

          yBuffer.get(nv21, 0, ySize)
          vuBuffer.get(nv21, ySize, vuSize)

          val yuvImage = YuvImage(nv21, ImageFormat.NV21, image.width, image.height, null)
          val outStream = ByteArrayOutputStream()

          yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 50, outStream)
          val imageBytes = outStream.toByteArray()

          return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }
}