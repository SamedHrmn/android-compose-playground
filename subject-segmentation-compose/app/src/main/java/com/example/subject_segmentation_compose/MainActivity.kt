package com.example.subject_segmentation_compose

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import com.example.subject_segmentation_compose.presentation.splash.SplashViewModel
import com.example.subject_segmentation_compose.ui.theme.Subject_segmentation_composeTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val splashViewModel: SplashViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {

        val splashScreen = installSplashScreen().apply {
            setOnExitAnimationListener { viewProvider ->
                ObjectAnimator.ofFloat(
                    viewProvider.view,
                    "scaleX",
                    0.5f, 0f
                ).apply {
                    interpolator = OvershootInterpolator()
                    duration = 300
                    doOnEnd { viewProvider.remove() }
                    start()
                }
                ObjectAnimator.ofFloat(
                    viewProvider.view,
                    "scaleY",
                    0.5f, 0f
                ).apply {
                    interpolator = OvershootInterpolator()
                    duration = 300
                    doOnEnd { viewProvider.remove() }
                    start()
                }
            }
        }

        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition {
            splashViewModel.isSplashShow.value
        }

        setContent {
            Subject_segmentation_composeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        PhotoSelectView(activity = this@MainActivity)
                    }
                }
            }
        }
    }
}


@Composable
fun PhotoSelectView(modifier: Modifier = Modifier, activity: Activity?) {
    val segmentationViewModel = viewModel<SubjectSegmentationViewModel>()
    val coroutineScope = rememberCoroutineScope()
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            activity?.let {
                segmentationViewModel.setSelectedBitmapFromUri(
                    uri,
                    context = activity.applicationContext
                )
            }
        }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (segmentationViewModel.selectedBitmap.value != null){
            SubcomposeAsyncImage(
                modifier = Modifier
                    .width(200.dp).height(300.dp),
                contentScale = ContentScale.Fit,
                model = segmentationViewModel.selectedBitmap.value, contentDescription = null,
                loading = {
                    CircularProgressIndicator()
                }
            )
        }else if(segmentationViewModel.segmentationBitmap.value != null){
            SubcomposeAsyncImage(
                modifier = Modifier
                    .width(200.dp).height(300.dp),
                contentScale = ContentScale.Fit,
                model = segmentationViewModel.segmentationBitmap.value, contentDescription = null,
            )
        }
        ElevatedButton(
            onClick = {
                coroutineScope.launch {
                    launcher.launch(PickVisualMediaRequest(mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly))
                }
            }) {
            Text(text = "Select Image", color = Color.Black)
        }
        ElevatedButton(
            enabled = segmentationViewModel.selectedBitmap.value != null,
            onClick = {
                 if(segmentationViewModel.selectedBitmap.value != null){
                     segmentationViewModel.processImage()
                 }
            }) {
            Text(text = "Process Image", color = Color.Black)
        }
    }
}

@Composable
@Preview(showBackground = true)
fun PhotoSelectViewPreview() {
    Subject_segmentation_composeTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                PhotoSelectView(activity = null)
            }
        }
    }
}