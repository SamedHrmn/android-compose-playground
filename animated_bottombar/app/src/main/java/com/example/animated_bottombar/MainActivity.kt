package com.example.animated_bottombar


import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.currentComposer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.example.animated_bottombar.ui.theme.Animated_bottombarTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Animated_bottombarTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        AnimatedBottombar(
                            modifier = Modifier.align(Alignment.BottomCenter),
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedBottombar(modifier: Modifier = Modifier) {
    var selectedItem by remember {
        mutableStateOf(0)
    }

    var previousItem by remember {
        mutableStateOf(0)
    }

    val navbarItems = listOf(
        Icons.Outlined.Home,
        Icons.Outlined.ShoppingCart,
        Icons.Outlined.Person
    )


    val tabWidth = calculateTabWidth(numberOfTabs = navbarItems.size)

    val indicatorOffsetAnim = animateDpAsState(targetValue = selectedItem * tabWidth, label = "")

    val childrenXPositions = remember { mutableStateListOf<Dp>() }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(85.dp)
            .background(color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)),
    ) {

        Box(
            modifier = Modifier
                .layout { measurable, constraints ->

                    val placeable = measurable.measure(constraints)
                    layout(placeable.width, placeable.height) {
                        if (childrenXPositions.isNotEmpty()) {
                            when (selectedItem) {
                                0 -> placeable.place(childrenXPositions[0].roundToPx(), 0)
                                1 -> placeable.place(childrenXPositions[1].roundToPx(), 0)
                                2 -> placeable.place(childrenXPositions[2].roundToPx(), 0)
                            }
                        }
                    }
                }
                .offset(x = indicatorOffsetAnim.value)

        ) {
            Box(
                modifier = Modifier
                    .height(4.dp)
                    .width(50.dp)
                    .background(
                        MaterialTheme.colorScheme.primary
                    )
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxSize(),
        ) {
            navbarItems.forEachIndexed { index, icon ->
                NavbarButton(
                    modifier = Modifier.onGloballyPositioned {
                        childrenXPositions.add(it.positionInRoot().x.dp)
                    },
                    selected = selectedItem == index,
                    onClick = {
                        previousItem = selectedItem
                        selectedItem = index

                    },
                    icon = icon
                )
            }
        }
    }
}


@Composable
fun calculateTabWidth(numberOfTabs: Int): Dp {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    return (screenWidth / numberOfTabs)
}

@Composable
fun NavbarButton(
    onClick: () -> Unit,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    size: Dp = 48.dp,
) {
    val bgAnimatedColor by animateColorAsState(
        targetValue = if (selected) Color.Green else MaterialTheme.colorScheme.background,
        label = "",
        animationSpec = tween(
            durationMillis = 300
        )
    )

    val animatedIconSize by animateDpAsState(
        targetValue = if (selected) size else 40.dp,
        label = ""
    )

    val interactionSource = remember {
        MutableInteractionSource()
    }

    Box(
        modifier = modifier
            .size(animatedIconSize)
            .clip(CircleShape)
            .background(bgAnimatedColor)
            .clickable(interactionSource, indication = rememberRipple(), onClick = onClick)
            .padding(4.dp)

    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

