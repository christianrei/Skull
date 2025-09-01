package com.dimmaranch.skull.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.dimmaranch.skull.AdManager
import com.dimmaranch.skull.commonUI.LeaveGameButton
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

class GameScreen(
    private val viewModel: GameViewModel,
    private val onEndGame: () -> Unit,
    private val adManager: AdManager,
) : Screen {

    @Composable
    override fun Content() {
        val gameState: GameState by viewModel.gameState.collectAsState()
        adManager.loadInterstitialAd()

        LeaveGameButton(gameVM = viewModel, navigator = LocalNavigator.current, adManager = adManager)

        val players = gameState.players.values.toList()
        val isCurrentUserTurn =
            gameState.isCurrentUserPlayer(viewModel.getCurrentUserId().orEmpty())
        when (gameState.phase) {
            Phase.PLACING_FIRST_CARD -> PlacingPhaseUI(viewModel, true)
            Phase.PLACING -> PlacingPhaseUI(viewModel)
            Phase.BIDDING -> BiddingPhaseUI(viewModel)
            Phase.CHALLENGING -> {
                ChallengingPhaseUI(
                    gameState = gameState,
                    players = players,
                    bidWinner = players[gameState.currentPlayerIndex],
                    skullOwner = players[gameState.challengedPlayerIndex],
                    isCurrentUserTurn = isCurrentUserTurn,
                    onAnimationEnded = {
                        viewModel.handleAction(GameAction.RevealAnimationDone)
//                    viewModel.clearRevealedCards()
                        // optionally continue the revealing logic
                    },
                    onCardSelected = { playerId, cardIndex ->
                        viewModel.handleAction(
                            GameAction.RevealNextCard(
                                playerId,
                                cardIndex
                            )
                        )
                    })
            }

            Phase.LOSE_A_CARD -> {
                LoseACardPhaseUI(
                    gameState = gameState,
                    losingPlayer = players[gameState.currentBidderIndex], //currentPlayerIndex is for the skullOwner?
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
                    adManager.showInterstitialAd()
                    viewModel.clearGame()
                    onEndGame.invoke()
                }
            )

            Phase.SETUP -> {
                //Not possible
            }
        }
    }
}
