package com.example.extended_fab_button

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope


import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color


import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.extended_fab_button.ui.theme.Extended_fab_buttonTheme

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Extended_fab_buttonTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ShaderContainer(modifier = Modifier.fillMaxSize().padding(end = 16.dp, bottom = 24.dp)) {

                        var expanded: Boolean by remember {
                            mutableStateOf(false)
                        }


                        val alpha by animateFloatAsState(
                            targetValue = if (expanded) 1f else 0f,
                            animationSpec = tween(durationMillis = 1000, easing = LinearEasing),
                            label = ""
                        )

                        ButtonComponent(
                            modifier = Modifier.padding(
                                PaddingValues(bottom = 80.dp) * FastOutSlowInEasing
                                    .transform((alpha))
                            ),
                            imageVector = Icons.Default.Edit,
                            alpha = alpha,
                            onClick = {
                                expanded = !expanded
                            })
                        ButtonComponent(
                            modifier = Modifier.padding(
                                PaddingValues(bottom = 160.dp) * FastOutSlowInEasing
                                    .transform((alpha))
                            ),
                            imageVector = Icons.Default.LocationOn,
                            alpha = alpha,
                            onClick = {
                                expanded = !expanded

                            })
                        ButtonComponent(
                            modifier = Modifier.padding(
                                PaddingValues(bottom = 240.dp) * FastOutSlowInEasing
                                    .transform((alpha)),
                            ),
                            imageVector = Icons.Default.Delete,
                            alpha = alpha,
                            onClick = {
                                expanded = !expanded

                            })
                        ButtonComponent(

                            imageVector = Icons.Default.Add,
                            alpha = 1f,
                            onClick = {
                                expanded = !expanded

                            }) {
                            val rotation by animateFloatAsState(
                                targetValue = if (expanded) 45f else 0f,
                                label = "",
                                animationSpec = tween(1000, easing = FastOutSlowInEasing)
                            )

                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.rotate(rotation)
                            )
                        }
                    }
                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun BoxScope.ButtonComponent(
    modifier: Modifier = Modifier,
    background: Color = Color.Black,
    imageVector: ImageVector,
    alpha: Float,
    onClick: () -> Unit,
    content: @Composable (BoxScope.() -> Unit)? = null,
) {
    BlurContainer(modifier = modifier
        .clickable(
            interactionSource = remember {
                MutableInteractionSource()
            },
            indication = null,
            onClick = onClick,
        )
        .align(Alignment.BottomEnd),
        component = {
            Box(
                Modifier
                    .size(40.dp)
                    .background(color = background, CircleShape)
            )
        },
        content = {
            Box(
                Modifier
                    .size(80.dp), contentAlignment = Alignment.Center
            ) {
                if (content != null) {
                    content()
                } else {
                    Icon(
                        imageVector = imageVector,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.alpha(alpha)
                    )
                }
            }
        })
}

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun BlurContainer(
    modifier: Modifier = Modifier,
    blur: Float = 60f,
    component: @Composable BoxScope.() -> Unit,
    content: @Composable BoxScope.() -> Unit = {},
) {
    Box(modifier, contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .customBlur(blur),
            content = component,
        )
        Box(
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}

@RequiresApi(Build.VERSION_CODES.S)
fun Modifier.customBlur(blur: Float) = this.then(
    graphicsLayer {
        if (blur > 0f)
            renderEffect = RenderEffect
                .createBlurEffect(
                    blur,
                    blur,
                    Shader.TileMode.DECAL,
                )
                .asComposeRenderEffect()
    }
)

private operator fun PaddingValues.times(factor: Float): PaddingValues {
    return PaddingValues(
        start = this.calculateStartPadding(LayoutDirection.Ltr) * factor,
        end = this.calculateEndPadding(LayoutDirection.Ltr) * factor,
        top = this.calculateTopPadding() * factor,
        bottom = this.calculateBottomPadding() * factor
    )
}
