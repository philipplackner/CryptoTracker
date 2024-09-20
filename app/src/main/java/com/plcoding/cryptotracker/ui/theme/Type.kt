package com.plcoding.cryptotracker.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.plcoding.cryptotracker.R

val SpaceMono = FontFamily(
    Font(
        resId = R.font.space_mono_regular,
        weight = FontWeight.Normal
    ),
    Font(
        resId = R.font.space_mono_italic,
        weight = FontWeight.Normal,
        style = FontStyle.Italic
    ),
    Font(
        resId = R.font.space_mono_bold,
        weight = FontWeight.Bold
    ),
    Font(
        resId = R.font.space_mono_bold_italic,
        weight = FontWeight.Bold,
        style = FontStyle.Italic
    ),
)

// Set of Material typography styles to start with
val Typography = Typography(
    bodySmall = TextStyle(
        fontFamily = SpaceMono,
        fontWeight = FontWeight.Light,
        fontSize = 12.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = SpaceMono,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = SpaceMono,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    labelMedium = TextStyle(
        fontFamily = SpaceMono,
        fontWeight = FontWeight.Normal,
    ),
    headlineMedium = TextStyle(
        fontFamily = SpaceMono,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp
    )
)