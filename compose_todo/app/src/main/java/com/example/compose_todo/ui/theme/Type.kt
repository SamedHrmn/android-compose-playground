package com.example.compose_todo.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.compose_todo.R

val fonts = FontFamily(
    Font(R.font.inter_light, weight = FontWeight(300)),
    Font(R.font.inter_regular, weight = FontWeight(400)),
    Font(R.font.inter_medium, weight = FontWeight( 500)),
    Font(R.font.inter_semibold, weight = FontWeight( 600)),
    Font(R.font.inter_bold, weight = FontWeight( 700)),
)

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = fonts,
        fontWeight = FontWeight(500),
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
        color = TextColorBlack
    ),
    bodySmall = TextStyle(
        fontFamily = fonts,
        fontWeight = FontWeight(300),
        fontSize = 12.sp,
        lineHeight = 16.sp,
        color = TextColorBlack,
        letterSpacing = 0.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = fonts,
        fontWeight = FontWeight(400),
        fontSize = 16.sp,
        lineHeight = 16.sp,
        color = TextColorBlack,
        letterSpacing = 0.sp
    ),
    displayLarge = TextStyle(
        fontFamily = fonts,
        fontWeight = FontWeight(600),
        fontSize = 16.sp,
        lineHeight = 17.sp,
        letterSpacing = 0.sp,
        color = TextColorBlack
    ),
    titleLarge = TextStyle(
        fontFamily = fonts,
        fontWeight = FontWeight(700),
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp,
        color = TextColorBlack
    ),


)