package com.dimmaranch.skull.phaseUI

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dimmaranch.skull.state.GameState
import com.dimmaranch.skull.state.Player
import com.dimmaranch.skull.commonUI.PokerTable
import com.dimmaranch.skull.commonUI.RevealCardsAnimation
import com.dimmaranch.skull.commonUI.Theme.defaultTextStyle

@Composable
fun ChallengingPhaseUI(
    gameState: GameState,
    players: List<Player>,
    losingPlayer: Player,
    skullOwner: Player,
    onAnimationEnded: () -> Unit,
    onCardSelected: (playerId: String, cardIndex: Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "${players[gameState.currentPlayerIndex].name} is selecting cards to reveal",
            style = defaultTextStyle,
            textAlign = TextAlign.Center
        )
        Text(
            text = "${gameState.remainingCardsToReveal} card(s) remain",
            style = defaultTextStyle,
            textAlign = TextAlign.Center
        )
        PokerTable(
            players = gameState.players.values.toList(),
            currentPhase = gameState.phase,
            bidWinnerId = losingPlayer.id,
            skullOwnerId = skullOwner.id,
            losingPlayerId = losingPlayer.id,
            onCardSelected = onCardSelected
        )
    }
    RevealCardsAnimation(
        revealedCards = gameState.revealedCards,
        onAnimationEnd = onAnimationEnded
    )
}