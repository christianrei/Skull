package com.dimmaranch.skulls.phaseUI

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dimmaranch.skulls.state.Card
import com.dimmaranch.skulls.state.Phase
import com.dimmaranch.skulls.state.Player
import com.dimmaranch.skulls.commonUI.PokerTable
import com.dimmaranch.skulls.commonUI.Theme.defaultTextStyle

@Composable
fun LoseACardPhaseUI(
    losingPlayer: Player,
    skullOwner: Player,
    onCardSelected: (String, Int) -> Unit,
    eliminatedPlayers: List<Player>,
    allPlayers: List<Player>
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "${losingPlayer.name} must discard a card!",
            style = defaultTextStyle,
            fontWeight = FontWeight.Bold,
            color = Color.Red
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Show all players around a poker table
        PokerTable(
            players = allPlayers,
            currentPhase = Phase.LOSE_A_CARD,
            bidWinnerId = losingPlayer.id,
            skullOwnerId = skullOwner.id,
            losingPlayerId = losingPlayer.id,
            onCardSelected = onCardSelected
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (losingPlayer.cardsInHand.isNotEmpty()) {
            Text("${skullOwner.name}, choose a card to remove:", fontWeight = FontWeight.Bold)
            LazyRow {
                itemsIndexed(losingPlayer.cardsInHand) { index, card ->
                    CardView(card, onClick = { onCardSelected(losingPlayer.id, index) })
                }
            }
        } else {
            Text("${losingPlayer.name} has no more cards and is eliminated!", color = Color.Red)
        }

        if (eliminatedPlayers.isNotEmpty()) {
            Text("Eliminated Players:", fontWeight = FontWeight.Bold, color = Color.Gray)
            eliminatedPlayers.forEach { player ->
                Text("- ${player.name}", color = Color.Gray)
            }
        }
    }
}

@Composable
fun CardView(card: Card, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(80.dp)
            .clickable { onClick() }
            .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Text(text = card.name, fontWeight = FontWeight.Bold)
    }
}