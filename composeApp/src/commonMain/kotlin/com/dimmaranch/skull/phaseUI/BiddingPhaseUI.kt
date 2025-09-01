package com.dimmaranch.skull.phaseUI

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dimmaranch.skull.commonUI.BidStepper
import com.dimmaranch.skull.state.GameAction
import com.dimmaranch.skull.viewmodel.GameViewModel
import com.dimmaranch.skull.commonUI.Theme.defaultTextStyle
import com.dimmaranch.skull.state.isCurrentUserPlayer

@Composable
fun BiddingPhaseUI(viewModel: GameViewModel) {
    val gameState by viewModel.gameState.collectAsState()
    val players = gameState.players.values.toList()

    Column(
        modifier = Modifier.fillMaxSize().padding(vertical = 54.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Bidding Phase - Current Player: ${players[gameState.currentPlayerIndex].name}",
            style = defaultTextStyle,
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Current Highest Bid: ${gameState.highestBid ?: 0}",
            style = defaultTextStyle,
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Number of cards placed: ${gameState.placedCards.size}",
            style = defaultTextStyle,
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Increase Bid",
                style = defaultTextStyle,
            )
            BidStepper(
                currentBid = players[gameState.currentPlayerIndex].bid,
                cardsList = gameState.placedCards,
                isCurrentTurn = gameState.isCurrentUserPlayer(viewModel.getCurrentUserId().orEmpty()),
                onBidChange = { newBid ->
                    viewModel.handleAction(GameAction.PlaceBid(newBid))
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.handleAction(GameAction.PassTurn) },
            modifier = Modifier.padding(8.dp),
            enabled = gameState.isCurrentUserPlayer(viewModel)
        ) {
            Text("Pass Turn")
        }
    }
}