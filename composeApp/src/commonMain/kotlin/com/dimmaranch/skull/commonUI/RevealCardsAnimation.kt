package com.dimmaranch.skull.commonUI

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.dimmaranch.skull.state.RevealedCard
import kotlinx.coroutines.delay

@Composable
fun RevealCardsAnimation(
    revealedCards: List<RevealedCard>,
    playerIndex: Int,
    onAnimationEnd: () -> Unit
) {
    val lastCard = revealedCards.lastOrNull()
    var animateLastCard by remember(lastCard) { mutableStateOf(false) }

    if (revealedCards.isNotEmpty()) {
        LaunchedEffect(lastCard) {
            animateLastCard = false
            delay(200) // slight buffer to ensure new card is drawn first
            animateLastCard = true
            delay(1000)
            onAnimationEnd()
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                val revealed = revealedCards.lastOrNull() ?: return@Row
                val isLast = revealed == lastCard
                FlipCard(
                    front = {
                        CardView(
                            card = revealed.card,
                            playerIndex,
                            isSelectable = false,
                            isAnimating = true,
                            isFaceUp = false
                        )
                    },
                    back = {
                        CardView(
                            card = revealed.card,
                            playerIndex,
                            isSelectable = false,
                            isAnimating = true,
                            isFaceUp = true
                        )
                    },
                    flip = if (isLast) animateLastCard else true // only animate last one
                )
            }
        }
    }
}


@Composable
fun FlipCard(
    front: @Composable () -> Unit,
    back: @Composable () -> Unit,
    flip: Boolean
) {
    val rotation by animateFloatAsState(
        targetValue = if (flip) 180f else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "cardRotation"
    )

    val isFront = rotation <= 90f

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .width(80.dp)
            .height(120.dp)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12 * density
            }
    ) {
        if (isFront) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        alpha = 1f
                        rotationY = 0f
                    }
            ) {
                front()
            }
        } else {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        alpha = 1f
                        rotationY = 180f
                    }
            ) {
                back()
            }
        }
    }
}
