package com.dimmaranch.skull

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TurnTimer(remainingTime: Float, maxTime: Float) {
    LinearProgressIndicator(progress = remainingTime / maxTime, modifier = Modifier.height(24.dp).fillMaxWidth())
}