package com.dimmaranch.skulls.commonUI

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
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
import androidx.compose.ui.unit.sp
import com.dimmaranch.skulls.state.RevealedCard
import kotlinx.coroutines.delay

@Composable
fun RevealCardsAnimation(
    revealedCards: List<RevealedCard>,
    onAnimationEnd: () -> Unit
) {
    var flipAllCards by remember { mutableStateOf(false) }

    if (revealedCards.isNotEmpty()) {
        LaunchedEffect(Unit) {
            delay(500)
            flipAllCards = true
            delay(2000) // show flipped cards for 2s
            onAnimationEnd()
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                revealedCards.forEach { revealed ->
                    FlipCard(
                        front = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.DarkGray),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Card Back", color = Color.White, fontSize = 12.sp)
                            }
                        },
                        back = {
                            //TODO Fix this with revealed = true not isSelectable
                            CardView(card = revealed.card, true, onClick = {})
                        },
                        flip = flipAllCards
                    )
                }
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
