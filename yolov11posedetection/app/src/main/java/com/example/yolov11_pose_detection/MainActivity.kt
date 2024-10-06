package com.example.yolov11_pose_detection

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
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yolov11_pose_detection.composable.CheckCameraPermission
import com.example.yolov11_pose_detection.composable.OverlayViewComposable
import com.example.yolov11_pose_detection.ui.theme.Yolov11posedetectionTheme
import com.example.yolov11_pose_detection.viewmodel.CameraPreviewViewModel
import java.util.concurrent.Executors

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Yolov11posedetectionTheme {
                MyApp(
                    application = application,
                    isFront = false,
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MyApp(
    application: Application,
    isFront: Boolean = false,
) {
    Scaffold(modifier = Modifier.safeDrawingPadding()) {
        CheckCameraPermission {
            CameraPreviewView(application, isFront)
        }
    }
}

@Composable
@SuppressLint("RestrictedApi")
fun SetupCamera(
    application: Application,
    previewView: PreviewView,
    isFront: Boolean = false,
) {

    val viewModel: CameraPreviewViewModel =
        viewModel(factory = CameraPreviewViewModel.Companion.Factory(application))

    val cameraProviderFuture = remember {
        ProcessCameraProvider.getInstance(application)
    }

    val lifecycleOwner = LocalLifecycleOwner.current

    val cameraExecutor = remember {
        Executors.newSingleThreadExecutor()
    }

    LaunchedEffect(Unit) {
        val cameraProvider = cameraProviderFuture.get()


        val preview = Preview.Builder()
            .setTargetRotation(previewView.rotation.toInt())
            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .build()


        val imageAnalyzer = ImageAnalysis.Builder()
            .setTargetRotation(previewView.rotation.toInt())
            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
            .build()


        preview.surfaceProvider = previewView.surfaceProvider

        imageAnalyzer.setAnalyzer(cameraExecutor) { imageProxy ->


            val bitmapBuffer = Bitmap.createBitmap(

                imageProxy.width,
                imageProxy.height,
                Bitmap.Config.ARGB_8888,
            )

            bitmapBuffer.copyPixelsFromBuffer(imageProxy.planes[0].buffer)

            val rotatedImageMatrix: Matrix = Matrix().apply {
                postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())
                if (isFront) {
                    postScale(-1f, 1f, imageProxy.width.toFloat(), imageProxy.height.toFloat())
                }
            }


            val rotatedBitmap = Bitmap.createBitmap(
                bitmapBuffer,
                0,
                0,
                bitmapBuffer.width,
                bitmapBuffer.height,
                rotatedImageMatrix,
                true,
            )


            viewModel.detect(rotatedBitmap)
            imageProxy.close()
        }

        val cameraSelector = if (isFront) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }
        cameraProvider.unbindAll()

        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            imageAnalyzer,

            )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("RestrictedApi")
@Composable
fun CameraPreviewView(
    application: Application,
    isFront: Boolean = false,
) {
    val previewView: PreviewView = remember {
        PreviewView(application)
    }

    val viewModel: CameraPreviewViewModel =
        viewModel(factory = CameraPreviewViewModel.Companion.Factory(application))


    val results by viewModel.detections.collectAsState()
    val inferenceTime by viewModel.inferenceTime.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .aspectRatio(3 / 4f)
    ) {
        SetupCamera(
            application = application,
            previewView = previewView,
            isFront = isFront,
        )
        AndroidView(factory = {
            previewView
        }, modifier = Modifier.fillMaxSize())
        OverlayViewComposable(results)
        Text(
            "$inferenceTime ms",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .background(color = Color.Red),
        )
    }
}