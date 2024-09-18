package com.example.efficientdet_detector

import android.app.Activity
import android.app.Application
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.efficientdet_detector.data.Detection
import com.example.efficientdet_detector.ui.common.DrawBoundingBox
import com.example.efficientdet_detector.ui.common.ScaffoldSheetContent
import com.example.efficientdet_detector.ui.theme.EfficientdetdetectorTheme
import com.example.efficientdet_detector.ui.theme.Typography
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch
import java.util.concurrent.Executors


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EfficientdetdetectorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    CameraPreviewView(this@MainActivity)
                }
            }
        }
    }
}

@kotlin.OptIn(ExperimentalPermissionsApi::class)
@OptIn(ExperimentalGetImage::class)
@Composable
fun CameraPreviewView(activity: Activity) {
    val permissionState = rememberPermissionState(android.Manifest.permission.CAMERA)

    if (!permissionState.hasPermission) {
        LaunchedEffect(Unit) {
            permissionState.launchPermissionRequest()
        }
    } else {
        CameraPreviewWithDetection(activity = activity)
    }
}

@Composable
fun CameraPreviewWithDetection(activity: Activity) {
    val mainActivityViewModel: MainActivityViewModel =
        viewModel(factory = MainActivityViewModel.Companion.Factory(activity.application))


    LaunchedEffect(Unit) {
        mainActivityViewModel.createObjectDetector()
    }


    val context = LocalContext.current
    val previewView: PreviewView = remember { PreviewView(context) }

    val detection = remember { mutableStateOf<Detection?>(null) }



    SetupCameraAnalyzer(

        previewView = previewView,
        onDetection = { detection.value = it },
        viewModel = mainActivityViewModel
    )

    CameraPreviewUI(
        previewView = previewView,
        detection = detection.value,
        application = activity.application
    )
}

@Composable
fun SetupCameraAnalyzer(
    previewView: PreviewView,
    onDetection: (Detection?) -> Unit,
    viewModel: MainActivityViewModel
) {
    val context = previewView.context
    val cameraProviderFuture =
        remember { ProcessCameraProvider.getInstance(context) }
    val executor = remember { Executors.newSingleThreadExecutor() }
    val lifecycleOwner = LocalLifecycleOwner.current


    LaunchedEffect(cameraProviderFuture) {
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val resolutionSelector = ResolutionSelector.Builder().setAspectRatioStrategy(
                AspectRatioStrategy.RATIO_4_3_FALLBACK_AUTO_STRATEGY
            ).build()

            val preview = Preview.Builder()
                .setResolutionSelector(resolutionSelector)
                .setTargetRotation(previewView.rotation.toInt())
                .build()

            preview.setSurfaceProvider(previewView.surfaceProvider)


            val imageAnalysis = ImageAnalysis.Builder()
                .setResolutionSelector(resolutionSelector)
                .setTargetRotation(previewView.rotation.toInt())
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .build()



            imageAnalysis.setAnalyzer(executor) { imageProxy ->

                val bitmapBuffer =
                    Bitmap.createBitmap(
                        imageProxy.width,
                        imageProxy.height,
                        Bitmap.Config.ARGB_8888
                    )
                imageProxy.use { bitmapBuffer.copyPixelsFromBuffer(imageProxy.planes[0].buffer) }
                imageProxy.close()

                val rotatedImageMatrix: Matrix =
                    Matrix().apply {
                        postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())
                    }


                val rotatedBitmap: Bitmap = Bitmap.createBitmap(
                    bitmapBuffer,
                    0,
                    0,
                    bitmapBuffer.width,
                    bitmapBuffer.height,
                    rotatedImageMatrix,
                    true
                )

                onDetection(viewModel.detectObject(rotatedBitmap))
            }
            val cameraSelector: CameraSelector = CameraSelector.Builder().build()

            cameraProvider.unbindAll()

            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageAnalysis
            )

            preview.setSurfaceProvider(previewView.surfaceProvider)

        }, ContextCompat.getMainExecutor(context))
    }
}

@Composable
fun CameraPreviewUI(previewView: PreviewView, detection: Detection?, application: Application) {
    PageScaffold(
        application = application,
        content = {
            Box(modifier = Modifier.fillMaxSize()) {
                AndroidView(factory = {
                    previewView
                }, modifier = Modifier.fillMaxSize())
                if (detection != null) {
                    DrawBoundingBox(detection)
                    Text(
                        text = detection.inferenceTime + "ms",
                        modifier = Modifier.align(Alignment.TopEnd)
                    )

                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = Color.Black.copy(0.6f))
                            .align(Alignment.Center)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.align(
                                Alignment.Center
                            ),
                        ) {
                            CircularProgressIndicator()
                            Text(
                                text = "Loading",
                                style = Typography.bodyLarge.copy(color = Color.White)
                            )
                        }

                    }

                }

            }
        }
    )
}

@kotlin.OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PageScaffold(content: @Composable () -> Unit, application: Application) {
    val mainActivityViewModel: MainActivityViewModel =
        viewModel(factory = MainActivityViewModel.Companion.Factory(application))


    val scaffoldSheetState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(skipHiddenState = false)
    )

    val snackbarHostState = remember { SnackbarHostState() }

    val coroutineScope = rememberCoroutineScope()

    BottomSheetScaffold(
        modifier = Modifier.fillMaxHeight(),
        sheetPeekHeight = 100.dp,
        scaffoldState = scaffoldSheetState,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        sheetContent = {
            ScaffoldSheetContent(scaffoldSheetState, onItemSelected = {
                coroutineScope.launch {
                    mainActivityViewModel.updateSelectedModel(it)
                }.invokeOnCompletion { _ ->
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            message = "Model change to ${it.modelName}",
                        )
                    }
                }
            })
        }

    ) {
        content()
    }
}
