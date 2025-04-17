package com.dimmaranch.skull

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import com.dimmaranch.skulls.AdManager

expect fun getCustomFontFamily(): FontFamily
expect fun provideAdManager(): AdManager
expect fun getScreenWidth(): Int
expect fun getScreenHeight(): Int
@Composable
expect fun handleBackPress()
@Composable
expect fun Float.toPx(): Float
