package com.dimmaranch.skull.phaseUI

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.dimmaranch.skull.state.Card
import com.dimmaranch.skull.state.Phase
import com.dimmaranch.skull.state.Player
import com.dimmaranch.skull.commonUI.PokerTable
import com.dimmaranch.skull.commonUI.Theme.defaultTextStyle
import com.dimmaranch.skull.state.GameState

@Composable
fun LoseACardPhaseUI(
    gameState: GameState,
    losingPlayer: Player,
    skullOwner: Player,
    isCurrentUserTurn: Boolean,
    onCardSelected: (String, Int) -> Unit,
    eliminatedPlayers: List<Player>,
    allPlayers: List<Player>
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
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
            bidWinner = losingPlayer,
            skullOwner = skullOwner,
            losingPlayer = losingPlayer,
            placedCards = gameState.placedCards,
            isCurrentUserTurn = isCurrentUserTurn,
            onCardSelected = onCardSelected
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (losingPlayer.cardsInHand.isEmpty()) {
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