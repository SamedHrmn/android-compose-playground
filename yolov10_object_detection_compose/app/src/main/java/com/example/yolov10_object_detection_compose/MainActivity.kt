package com.example.yolov10_object_detection_compose

import android.annotation.SuppressLint
import android.app.Application
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yolov10_object_detection_compose.composable.BoundingBox
import com.example.yolov10_object_detection_compose.composable.CheckCameraPermission
import com.example.yolov10_object_detection_compose.ui.theme.Yolov10_object_detection_composeTheme
import com.example.yolov10_object_detection_compose.viewmodel.CameraPreviewViewModel
import java.util.concurrent.Executors

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Yolov10_object_detection_composeTheme {
                MyApp(application = application)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MyApp(application: Application) {
    val cameraPreviewViewModel: CameraPreviewViewModel =
        viewModel(factory = CameraPreviewViewModel.Companion.Factory(application))

    Scaffold(modifier = Modifier.safeDrawingPadding()) {
        CheckCameraPermission {
            CameraPreviewView(
                application = application,
                cameraPreviewViewModel = cameraPreviewViewModel
            )
        }
    }
}

@Composable
@SuppressLint("RestrictedApi")
fun SetupCamera(
    application: Application,
    previewView: PreviewView,
    cameraPreviewViewModel: CameraPreviewViewModel
) {


    val cameraProviderFuture = remember {
        ProcessCameraProvider.getInstance(application)
    }
    val executor = remember {
        Executors.newSingleThreadExecutor()
    }
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(Unit) {
        cameraProviderFuture.addListener({

            val cameraProvider = cameraProviderFuture.get()
            val resolutionSelector = ResolutionSelector.Builder().setAspectRatioStrategy(
                AspectRatioStrategy.RATIO_4_3_FALLBACK_AUTO_STRATEGY
            ).build()
            val preview = Preview.Builder().setResolutionSelector(resolutionSelector)
                .setTargetRotation(previewView.rotation.toInt())
                .build()

            preview.setSurfaceProvider(previewView.surfaceProvider)

            val imageAnalysis = ImageAnalysis.Builder()
                .setResolutionSelector(resolutionSelector)
                .setTargetRotation(previewView.rotation.toInt())
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .build()

            imageAnalysis.setAnalyzer(executor) { imageProxy ->


                val bitmapBuffer = Bitmap.createBitmap(

                    imageProxy.width,
                    imageProxy.height,
                    Bitmap.Config.ARGB_8888,

                    )

                bitmapBuffer.copyPixelsFromBuffer(imageProxy.planes[0].buffer)

                val rotatedImageMatrix: Matrix = Matrix().apply {
                    postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())
                }

                imageProxy.close()

                val rotatedBitmap = Bitmap.createBitmap(
                    bitmapBuffer,
                    0,
                    0,
                    bitmapBuffer.width,
                    bitmapBuffer.height,
                    rotatedImageMatrix,
                    true,
                )

                cameraPreviewViewModel.detectObject(rotatedBitmap)
            }

            val cameraSelector = CameraSelector.Builder().build()
            cameraProvider.unbindAll()

            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageAnalysis,
            )
        }, ContextCompat.getMainExecutor(application))
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("RestrictedApi")
@Composable
fun CameraPreviewView(application: Application, cameraPreviewViewModel: CameraPreviewViewModel) {
    val previewView: PreviewView = remember {
        PreviewView(application)
    }

    val detections = cameraPreviewViewModel.detections.toList()
    val inferenceTime = cameraPreviewViewModel.inferenceTime.longValue

    SetupCamera(
        application = application,
        previewView = previewView,
        cameraPreviewViewModel = cameraPreviewViewModel
    )

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())
        if (detections.isNotEmpty()) {
            BoundingBox(detections = detections, context = application)
            Box(Modifier.align(Alignment.TopEnd)) {
                Text(text = "$inferenceTime ms")
            }
        }
    }
}

