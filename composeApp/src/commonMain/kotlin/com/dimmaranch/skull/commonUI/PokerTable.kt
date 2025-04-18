package com.dimmaranch.skull.commonUI

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.dimmaranch.skull.commonUI.Theme.PokerTableGreen
import com.dimmaranch.skull.commonUI.Theme.defaultTextStyle
import com.dimmaranch.skull.state.Card
import com.dimmaranch.skull.state.Phase
import com.dimmaranch.skull.state.Player
import org.jetbrains.compose.resources.painterResource
import skull.composeapp.generated.resources.Res
import skull.composeapp.generated.resources.blueskull
import skull.composeapp.generated.resources.redback
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
    onCardSelected: (String, Int) -> Unit = { _, _ -> }
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val width = constraints.maxWidth.toFloat()
        val height = constraints.maxHeight.toFloat()
        val density = LocalDensity.current

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
            val isSkullOwner = player.id == skullOwner?.id
            val showLosingCardsInCenter = currentPhase == Phase.LOSE_A_CARD && isLosingPlayer

            if (!showLosingCardsInCenter) {
                Box(
                    modifier = Modifier
                        .offset { IntOffset(x.roundToInt(), y.roundToInt()) }
                        .border(
                            2.dp,
                            if (player.id == bidWinner?.id && currentPhase == Phase.CHALLENGING) Color.Red
                            else Color.Transparent
                        )
                        .padding(8.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = player.name, color = Color.White)
                            if (player.points == 1) {
                                Icon(
                                    imageVector = Icons.Default.Star, // Change to a cooler icon if desired
                                    contentDescription = "1 point",
                                    tint = Color.Magenta,
                                    modifier = Modifier.size(24.dp).padding(start = 4.dp)
                                )
                            }
                        }

                        // Placed cards on the table (hidden or shown based on phase)
                        Row {
                            placedCards[player.id]?.forEachIndexed { idx, card ->
                                CardView(card, isCurrentUserTurn) {
                                    onCardSelected.invoke(player.id, idx)
                                }
                            }
                        }

                        if (player.cardsInHand.isNotEmpty()) {
                            // One face-down card to symbolize hand
                            Spacer(modifier = Modifier.height(4.dp))
                            Image(
                                painter = painterResource(Res.drawable.redback),
                                contentDescription = null,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
            }
        }

        // Show losing player's cards in center if in LOSING_CARD phase
        if (currentPhase == Phase.LOSE_A_CARD && losingPlayer?.id != null && skullOwner?.id != null) {
            val cards = losingPlayer?.cardsInHand ?: emptyList()
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (isCurrentUserTurn) "Tap a card to discard from ${losingPlayer?.name}" else "${losingPlayer?.name} is discarding...",
                    color = Color.Red,
                    style = defaultTextStyle,
                    modifier = Modifier.padding(8.dp)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    cards.forEachIndexed { idx, card ->
                        CardView(
                            card = card,
                            isFaceUp = false, //skullOwner?.id == currentPlayer.id,
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
fun CardView(
    card: Card,
    isSelectable: Boolean,
    isFaceUp: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    var showConfirmation by remember { mutableStateOf(false) }
    val cardImage = if (isFaceUp) painterResource(Res.drawable.blueskull) else painterResource(Res.drawable.redback)
    val clickableModifier = if (isSelectable) {
        PulsingBorder().clickable { onClick?.invoke() }
    } else {
        Modifier
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = clickableModifier
            .size(40.dp)
            .let { if (onClick != null) it.clickable { onClick() } else it }
            .clickable(enabled = isSelectable) { showConfirmation = true }
    ) {
        Image(
            painter = cardImage,
            contentDescription = null,
            modifier = Modifier.size(32.dp)
        )
        if (showConfirmation) {
            ConfirmationDialog(
                onConfirm = {
                    showConfirmation = false
                    onClick?.invoke()
                },
                onDismiss = { showConfirmation = false }
            )
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
