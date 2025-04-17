package com.dimmaranch.skulls.phaseUI

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dimmaranch.skulls.commonUI.BidStepper
import com.dimmaranch.skulls.state.GameAction
import com.dimmaranch.skull.viewmodel.GameViewModel
import com.dimmaranch.skulls.commonUI.Theme.defaultTextStyle
import com.dimmaranch.skulls.state.isCurrentUserPlayer

@Composable
fun BiddingPhaseUI(viewModel: GameViewModel) {
    val gameState by viewModel.gameState.collectAsState()
    val playerId by viewModel.userNameState.collectAsState()
    val players = gameState.players.values.toList()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
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

        Row {
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

//            Button(
//                onClick = { viewModel.handleAction(GameAction.Challenge(gameState.currentPlayerIndex)) },
//                modifier = Modifier.padding(8.dp)
//            ) {
//                Text("Challenge")
//            }
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