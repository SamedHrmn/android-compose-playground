package com.example.weekly_selectable_calendar

import android.graphics.BlurMaskFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.animation.Animatable
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.weekly_selectable_calendar.ui.theme.Weekly_selectable_calendarTheme
import kotlinx.coroutines.Delay
import kotlinx.coroutines.delay
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.util.Locale

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Weekly_selectable_calendarTheme {
                WeeklySelectableCalendar("Android")
            }
        }
    }
}

@OptIn(
    ExperimentalMaterial3Api::class
)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeeklySelectableCalendar(name: String, modifier: Modifier = Modifier) {
    var localDate = LocalDate.now()
    var weeklyDates by remember {
        mutableStateOf(generateWeeklyDates(localDate))
    }
    var selectedDate by remember {
        mutableStateOf(localDate)
    }


    fun checkIsSelected(date: LocalDate): Boolean = (selectedDate == date)

    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        val index = weeklyDates.indexOfFirst { it == LocalDate.now() }

        Log.d(
            "Layout Info today offset :",
            listState.layoutInfo.visibleItemsInfo.firstOrNull { item -> item.index == index }?.offset.toString()
        )

        Log.d("Layout Info list viewport end offset :", listState.layoutInfo.viewportEndOffset.toString())

        listState.layoutInfo.visibleItemsInfo.firstOrNull { item -> item.index == index }.also {
            it?.let { today ->
                if (today.offset < listState.layoutInfo.viewportEndOffset) {
                    delay(1000)
                    listState.animateScrollToItem(index = index)
                }
            }
        }
    }

    Surface(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(vertical = 24.dp)) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                        .format(selectedDate),
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(modifier.weight(1f))
                Row {
                    IconButton(onClick = {
                        localDate = localDate.minusWeeks(1)
                        weeklyDates = generateWeeklyDates(localDate)

                    }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = null)
                    }
                    IconButton(onClick = {
                        localDate = localDate.plusWeeks(1)
                        weeklyDates = generateWeeklyDates(localDate)


                    }) {
                        Icon(Icons.Filled.ArrowForward, contentDescription = null)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))



            LazyRow(
                contentPadding = PaddingValues(horizontal = 4.dp),
                state = listState,
            ) {
                items(
                    count = weeklyDates.size,
                    key = { weeklyDates[it].toEpochDay() }
                ) { i ->

                    val alphaAnimation = remember {
                        Animatable(0f)
                    }


                    LaunchedEffect(Unit) {
                        alphaAnimation.animateTo(1f)
                    }

                    Card(
                        modifier = Modifier
                            .width(90.dp)
                            .height(100.dp)
                            .padding(horizontal = if (i > 0 || i < weeklyDates.size) 8.dp else 0.dp)
                            .shadow(
                                offsetY = 8.dp,
                                blurRadius = 4.dp,
                                cornerRadius = 12.dp,
                                color = Color.Black.copy(alpha = if (alphaAnimation.value != 1f) 0f else 0.3f)
                            )
                            .alpha(alphaAnimation.value),

                        colors = CardDefaults.cardColors(
                            containerColor = if (checkIsSelected(weeklyDates[i])) MaterialTheme.colorScheme.onPrimaryContainer
                            else MaterialTheme.colorScheme.surfaceVariant
                        ),

                        onClick = {
                            selectedDate = weeklyDates[i]
                        }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = 12.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = weeklyDates[i].dayOfWeek.getDisplayName(
                                    TextStyle.SHORT,
                                    Locale.getDefault()
                                ),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = if (checkIsSelected(
                                            weeklyDates[i]
                                        )
                                    ) Color.White else Color.Black
                                ),
                            )
                            Text(
                                text = weeklyDates[i].dayOfMonth.toString(),
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = if (checkIsSelected(
                                            weeklyDates[i]
                                        )
                                    ) Color.White else Color.Black
                                ),
                            )
                        }
                    }
                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Weekly_selectable_calendarTheme {
        WeeklySelectableCalendar("Android")
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun generateWeeklyDates(startingDate: LocalDate): List<LocalDate> {
    val monday = startingDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    return (0 until 7).map { monday.plusDays(it.toLong()) }
}

fun Modifier.shadow(
    color: Color = Color.Black,
    offsetX: Dp = 0.dp,
    offsetY: Dp = 0.dp,
    blurRadius: Dp = 0.dp,
    cornerRadius: Dp = 0.dp,
) = then(
    drawBehind {
        drawIntoCanvas { canvas ->
            val paint = Paint()
            val frameworkPaint = paint.asFrameworkPaint()
            if (blurRadius != 0.dp) {
                frameworkPaint.maskFilter =
                    (BlurMaskFilter(blurRadius.toPx(), BlurMaskFilter.Blur.NORMAL))
            }
            frameworkPaint.color = color.toArgb()

            val leftPixel = offsetX.toPx()
            val topPixel = offsetY.toPx()
            val rightPixel = size.width + leftPixel
            val bottomPixel = size.height + topPixel

            canvas.drawRoundRect(
                left = leftPixel,
                top = topPixel,
                right = rightPixel,
                bottom = bottomPixel,
                radiusX = cornerRadius.toPx(),
                radiusY = cornerRadius.toPx(),
                paint = paint,
            )
        }
    }
)