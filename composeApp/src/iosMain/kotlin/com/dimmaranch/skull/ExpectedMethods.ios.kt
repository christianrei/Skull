package com.dimmaranch.skull

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIViewController
import androidx.compose.ui.text.platform.Font
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.*

actual fun getCustomFontFamily(): FontFamily {
    val fontName = "dizzyedgeDEMO.ttf"

    val path = NSBundle.mainBundle.pathForResource(name = fontName.removeSuffix(".ttf"), ofType = "ttf")
        ?: error("Font file not found in bundle: $fontName")

    val data = NSData.dataWithContentsOfFile(path)
        ?: error("Failed to load font data at path: $path")

    val byteArray = data.toByteArray()

    return FontFamily(Font(identity = fontName, data = byteArray))
}

@OptIn(ExperimentalForeignApi::class)
private fun NSData.toByteArray(): ByteArray {
    val length = this.length.toInt()
    val bytes = ByteArray(length)
    bytes.usePinned {
        this@toByteArray.getBytes(it.addressOf(0), length.toULong())
    }
    return bytes
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