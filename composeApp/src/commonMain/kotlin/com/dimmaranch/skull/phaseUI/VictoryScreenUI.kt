package com.dimmaranch.skull.phaseUI

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun VictoryScreenUI(
    players: List<String>,
    winner: String,
    onEndGame: () -> Unit // Callback to return to main menu
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAF3E0))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        ConfettiAnimation()

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "🎉 Game Over 🎉",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF444444)
                )

                players.forEach { player ->
                    if (player == winner) {
                        WinnerRow(name = player)
                    } else {
                        Text(
                            text = player,
                            fontSize = 20.sp,
                            color = Color(0xFF666666)
                        )
                    }
                }
            }

            Button(
                onClick = onEndGame,
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .height(56.dp)
                    .fillMaxWidth(0.6f)
            ) {
                Text("End Game", color = Color.White, fontSize = 18.sp)
            }
        }
    }
}


@Composable
fun WinnerRow(name: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("👑", fontSize = 28.sp)
        Text(
            text = name,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFD4AF37) // Gold
        )
    }
}

@Composable
fun ConfettiAnimation() {
    val infiniteTransition = rememberInfiniteTransition()
    val confettiOffsetY = infiniteTransition.animateFloat(
        initialValue = -200f,
        targetValue = 1200f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing)
        )
    )

    Canvas(
        modifier = Modifier.fillMaxSize()
    ) {
        val colors = listOf(Color.Red, Color.Blue, Color.Green, Color.Yellow, Color.Magenta)
        for (i in 0 until 100) {
            val x = (i * 13) % size.width
            val y = (confettiOffsetY.value + i * 20) % size.height
            drawCircle(
                color = colors[i % colors.size],
                radius = 6f,
                center = Offset(x, y)
            )
        }
    }
}

