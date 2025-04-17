package com.dimmaranch.skull.commonUI

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.dimmaranch.skull.state.Card
import com.dimmaranch.skull.state.Phase
import com.dimmaranch.skull.state.Player
import org.jetbrains.compose.resources.painterResource
import skull.composeapp.generated.resources.Res
import skull.composeapp.generated.resources.redback
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun PokerTable(
    players: List<Player>,
    currentPhase: Phase,
    bidWinnerId: String?,
    skullOwnerId: String?,
    losingPlayerId: String?,
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

            val isLosingPlayer = player.id == losingPlayerId
            val isSkullOwner = player.id == skullOwnerId
            val showLosingCardsInCenter = currentPhase == Phase.LOSE_A_CARD && isLosingPlayer

            if (!showLosingCardsInCenter) {
                Box(
                    modifier = Modifier
                        .offset { IntOffset(x.roundToInt(), y.roundToInt()) }
                        .border(
                            2.dp,
                            if (player.id == bidWinnerId && currentPhase == Phase.CHALLENGING) Color.Yellow
                            else Color.Transparent
                        )
                        .padding(4.dp)
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
        if (currentPhase == Phase.LOSE_A_CARD && losingPlayerId != null && skullOwnerId != null) {
            val cards = placedCards[losingPlayerId] ?: emptyList()
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Choose a card to discard", color = Color.Red)
                Row {
                    cards.forEachIndexed { idx, card ->
                        CardView(card, isCurrentUserTurn) {
                            onCardSelected.invoke(losingPlayerId, idx)
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun CardView(card: Card, isSelectable: Boolean, onClick: () -> Unit) {
    var showConfirmation by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .size(60.dp)
            .background(if (isSelectable) Color.Gray else Color.Transparent)
            .clickable(enabled = isSelectable) { showConfirmation = true }
    ) {
        Image(
            painter = painterResource(Res.drawable.redback),
            contentDescription = null,
            modifier = Modifier.size(32.dp)
        )
        if (showConfirmation) {
            ConfirmationDialog(
                onConfirm = {
                    showConfirmation = false
                    onClick.invoke()
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
