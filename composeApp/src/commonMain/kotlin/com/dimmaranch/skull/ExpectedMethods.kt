package com.dimmaranch.skull

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily

expect fun getCustomFontFamily(): FontFamily
expect fun getScreenWidth(): Int
expect fun getScreenHeight(): Int
@Composable
expect fun handleBackPress()
@Composable
expect fun Float.toPx(): Float
