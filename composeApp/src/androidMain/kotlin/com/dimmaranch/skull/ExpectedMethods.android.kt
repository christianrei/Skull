package com.dimmaranch.skull

import android.content.res.Resources
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

actual fun getCustomFontFamily(): FontFamily {
    return FontFamily(
        Font(
            resId = R.font.dizzyedge,
            weight = FontWeight.Normal,
        )
    )
}

actual fun getScreenWidth(): Int {
    return Resources.getSystem().displayMetrics.widthPixels
}

actual fun getScreenHeight(): Int {
    return Resources.getSystem().displayMetrics.heightPixels
}

@Composable
actual fun handleBackPress() {
    BackHandler(enabled = true) {
        // Handle Android-specific back behavior
    }
}

@Composable
actual fun Float.toPx(): Float {
    return with(LocalDensity.current) { this@toPx.dp.toPx() }
}