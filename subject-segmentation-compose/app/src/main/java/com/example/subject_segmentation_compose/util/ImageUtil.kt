package com.example.subject_segmentation_compose.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import coil.ImageLoader
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.request.SuccessResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

object ImageUtil {
    suspend fun uriToBitmap(
        uri: Uri,
        context: Context,
        scope: CoroutineScope,
        onSuccess: (bitmap: Bitmap) -> Unit
    ) {
        var bitmap: Bitmap? = null

        val loadBitmap = scope.launch(Dispatchers.IO) {
            val loader = ImageLoader(context)
            val request = ImageRequest.Builder(context).data(uri).build()
            val result = loader.execute(request)
            if (result is SuccessResult) {
                bitmap = (result.drawable as BitmapDrawable).bitmap
            } else if (result is ErrorResult) {
                cancel(result.throwable.localizedMessage ?: "ErrorResult", result.throwable)
            }
        }

        loadBitmap.invokeOnCompletion {
            bitmap?.let {
                onSuccess(it)
            }
        }
    }
}