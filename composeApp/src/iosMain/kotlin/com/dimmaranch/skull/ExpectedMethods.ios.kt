package com.dimmaranch.skull

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIViewController

actual fun getCustomFontFamily(): FontFamily {
    return FontFamily.Default // iOS uses system fonts or manually specified fonts
}

actual fun provideAdManager(): AdManager {
    return IosAdManager(UIViewController())
}

@OptIn(ExperimentalForeignApi::class)
actual fun getScreenWidth(): Int {
    return 0
    //return UIScreen.mainScreen.bounds.size.width.toInt()
}

@OptIn(ExperimentalForeignApi::class)
actual fun getScreenHeight(): Int {
    return 0
    //return UIScreen.mainScreen.bounds.size.height.toInt()
}

@Composable
actual fun handleBackPress() {
    // No back button on iOS, so do nothing
}

@Composable
actual fun Float.toPx(): Float = this