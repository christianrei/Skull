package com.dimmaranch.skull.commonUI

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
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
            val angle = (360f / players.size) * index - 90f // Make top the 0°
            val radians = angle * (PI / 180) // Kotlin multiplatform way

            val radiusDp = 150.dp
            val offset = with(density) { radiusDp.toPx() }
            val x = cos(radians) * offset
            val y = sin(radians) * offset

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
                    Text(text = player.name, color = Color.White)
                    Row {
                        // Cards placed can be tapped in not already revealed
                        player.cardsPlaced.forEachIndexed { idx, card ->
                            CardView(card, player.id == losingPlayerId && skullOwnerId == bidWinnerId) {
                                onCardSelected.invoke(player.id, idx)
                            }
                        }
                        // cards in hand should just be a pile thats not interactable
//                        player.cardsInHand.forEachIndexed { idx, card ->
//                            CardView(card, player.id == losingPlayerId && skullOwnerId == bidWinnerId) {}
//                        }
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
        if (showConfirmation) {
            ConfirmationDialog(onConfirm = onClick, onDismiss = { showConfirmation = false })
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
