package com.dimmaranch.skull.commonUI

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dimmaranch.skull.Utils
import com.dimmaranch.skull.commonUI.Theme.PokerTableGreen
import com.dimmaranch.skull.commonUI.Theme.defaultTextStyle
import com.dimmaranch.skull.state.Card
import com.dimmaranch.skull.state.Phase
import com.dimmaranch.skull.state.Player
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import skull.composeapp.generated.resources.Res
import skull.composeapp.generated.resources.transcrown
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun PokerTable(
    players: List<Player>,
    currentPhase: Phase,
    bidWinner: Player?,
    skullOwner: Player?,
    losingPlayer: Player?,
    isCurrentUserTurn: Boolean,
    placedCards: Map<String, List<Card>>,
    onCardSelected: (String, Int) -> Unit = { _, _ -> },
) {
    val density = LocalDensity.current
    val revealedCardIndices = remember { mutableStateMapOf<String, MutableSet<Int>>() }

    // Dramatic Auto Flip for Bid Winner
    LaunchedEffect(currentPhase, bidWinner?.id, isCurrentUserTurn) {
        val bidderId = bidWinner?.id
        if (currentPhase == Phase.CHALLENGING && isCurrentUserTurn && bidderId != null) {
            val cardsToReveal = placedCards[bidderId] ?: return@LaunchedEffect
            val revealed = revealedCardIndices.getOrPut(bidderId) { mutableSetOf() }

            for (i in cardsToReveal.indices) {
                if (revealed.contains(i)) continue
                delay(800)
                revealed.add(i)
            }
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val width = constraints.maxWidth.toFloat()
        val height = constraints.maxHeight.toFloat()

        // Poker Table background
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
        ) {
            drawOval(
                color = PokerTableGreen,
                size = Size(width * 0.8f, height * 0.6f),
                topLeft = Offset(
                    (size.width - width * 0.8f) / 2f,
                    (size.height - height * 0.6f) / 2f
                )
            )
        }

        players.forEachIndexed { index, player ->
            val angle = (360f / players.size) * index - 90f
            val radians = angle * (PI / 180)

            val radiusDp = 150.dp
            val offset = with(density) { radiusDp.toPx() }
            val x = cos(radians) * offset
            val y = sin(radians) * offset

            val isLosingPlayer = player.id == losingPlayer?.id
            val showLosingCardsInCenter = currentPhase == Phase.LOSE_A_CARD && isLosingPlayer

            if (!showLosingCardsInCenter) {
                Box(
                    modifier = Modifier
                        .offset { IntOffset(x.roundToInt(), y.roundToInt()) }
                        .padding(8.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (player.id == bidWinner?.id && currentPhase == Phase.CHALLENGING) {
                            Image(
                                painter = painterResource(Res.drawable.transcrown),
                                contentDescription = "Bid Winner Crown",
                                modifier = Modifier
                                    .size(48.dp)
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = player.name, color = Color.White)
                            if (player.points == 1) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "1 point",
                                    tint = Color.Magenta,
                                    modifier = Modifier.size(24.dp).padding(start = 4.dp)
                                )
                            }
                        }

                        // Placed cards
                        placedCards[player.id]?.let { cards ->
                            if (cards.isNotEmpty()) {
                                val topIndex = cards.lastIndex
                                val isBidder = player.id == bidWinner?.id
                                val revealedIndices = revealedCardIndices[player.id] ?: emptySet()
                                val isTopCardFaceUp = topIndex in revealedIndices

                                val isSelectable = currentPhase == Phase.CHALLENGING &&
                                        isCurrentUserTurn &&
                                        (((placedCards[bidWinner?.id]?.size
                                            ?: 0) >= 1 && isBidder) || (placedCards[bidWinner?.id]?.size
                                            ?: 0) == 0 && !isBidder)

                                println("MEME: isSelectable: $isSelectable")

                                Box(contentAlignment = Alignment.Center) {
                                    AnimatedCardFlip(
                                        isFaceUp = isTopCardFaceUp,
                                        front = {
                                            CardView(
                                                card = cards[topIndex],
                                                playerIndex = index,
                                                isSelectable = isSelectable,
                                                isFaceUp = false,
                                                onClick = {
                                                    onCardSelected(player.id, topIndex)
                                                }
                                            )
                                        },
                                        back = {
                                            CardView(
                                                card = cards[topIndex],
                                                playerIndex = index,
                                                isSelectable = isSelectable,
                                                isFaceUp = true,
                                                onClick = null
                                            )
                                        }
                                    )

                                    if (cards.size > 1) {
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .offset(x = 8.dp, y = (-8).dp)
                                                .background(Color.Red, CircleShape)
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "${cards.size}",
                                                color = Color.White,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Player's hand preview
                        if (player.cardsInHand.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Image(
                                painter = painterResource(Utils.mapPlayerIndexToDrawable(index)),
                                contentDescription = null,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
            }
        }

        // Center cards for discard phase
        if (currentPhase == Phase.LOSE_A_CARD && losingPlayer?.id != null && skullOwner?.id != null) {
            val cards = losingPlayer?.cardsInHand ?: emptyList()
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${losingPlayer?.name} has had their cards randomized!",
                    color = Color.Yellow,
                    style = defaultTextStyle,
                    modifier = Modifier.padding(8.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (isCurrentUserTurn) "Tap a card to discard from ${losingPlayer.name}" else "${losingPlayer.name} is discarding...",
                    color = Color.Red,
                    style = defaultTextStyle,
                    modifier = Modifier.padding(8.dp)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    cards.shuffled().forEachIndexed { idx, card ->
                        CardView(
                            card = card,
                            playerIndex = players.indexOf(losingPlayer),
                            isFaceUp = false,
                            isSelectable = isCurrentUserTurn,
                            onClick = {
                                onCardSelected.invoke(losingPlayer.id, idx)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ConfirmationDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirm Card Removal?") },
        confirmButton = {
            Button(onClick = onConfirm) { Text("Remove") }
        },
        dismissButton = {
            Button(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AnimatedCardFlip(
    isFaceUp: Boolean,
    front: @Composable () -> Unit,
    back: @Composable () -> Unit
) {
    val rotation by animateFloatAsState(
        targetValue = if (isFaceUp) 180f else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "cardFlip"
    )
    val isFront = rotation <= 90f
    val density = LocalDensity.current

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .width(80.dp)
            .height(120.dp)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12 * density.density
            }
    ) {
        if (isFront) {
            front()
        } else {
            back()
        }
    }
}

