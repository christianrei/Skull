package com.dimmaranch.skull.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.dimmaranch.skull.phaseUI.BiddingPhaseUI
import com.dimmaranch.skull.phaseUI.ChallengingPhaseUI
import com.dimmaranch.skull.phaseUI.LoseACardPhaseUI
import com.dimmaranch.skull.phaseUI.PlacingPhaseUI
import com.dimmaranch.skull.phaseUI.VictoryScreenUI
import com.dimmaranch.skull.state.GameAction
import com.dimmaranch.skull.state.GameState
import com.dimmaranch.skull.state.Phase
import com.dimmaranch.skull.state.isCurrentUserPlayer
import com.dimmaranch.skull.viewmodel.GameViewModel

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    onEndGame: () -> Unit
) {
    val gameState: GameState by viewModel.gameState.collectAsState()

    val players = gameState.players.values.toList()
    val isCurrentUserTurn = gameState.isCurrentUserPlayer(viewModel.getCurrentUserId().orEmpty())
    when (gameState.phase) {
        Phase.PLACING_FIRST_CARD -> PlacingPhaseUI(viewModel, true)
        Phase.PLACING -> PlacingPhaseUI(viewModel)
        Phase.BIDDING -> BiddingPhaseUI(viewModel)
        Phase.CHALLENGING -> {
            ChallengingPhaseUI(
                gameState = gameState,
                players = players,
                losingPlayer = players[gameState.currentPlayerIndex],
                skullOwner = players[gameState.challengedPlayerIndex],
                isCurrentUserTurn = isCurrentUserTurn,
                onAnimationEnded = {
                    viewModel.clearRevealedCards()
                    // optionally continue the revealing logic
                }
            ) { playerId, cardIndex ->
                viewModel.handleAction(
                    GameAction.RevealNextCard(
                        playerId,
                        cardIndex
                    )
                )
            }
        }

        Phase.LOSE_A_CARD -> {
            LoseACardPhaseUI(
                gameState = gameState,
                losingPlayer = players[gameState.currentPlayerIndex],
                skullOwner = players[gameState.challengedPlayerIndex],
                isCurrentUserTurn = isCurrentUserTurn,
                onCardSelected = { playerId, cardIndex ->
                    viewModel.handleAction(
                        GameAction.LoseCard(
                            playerId,
                            cardIndex
                        )
                    )
                },
                eliminatedPlayers = emptyList(),
                allPlayers = players
            )
        }

        Phase.END -> VictoryScreenUI(
            players.map { it.name },
            players[gameState.currentPlayerIndex].name,
            onEndGame = {
                viewModel.clearGame()
                onEndGame.invoke()
            }
        )

        Phase.SETUP -> {
            //Not possible
        }
    }
}