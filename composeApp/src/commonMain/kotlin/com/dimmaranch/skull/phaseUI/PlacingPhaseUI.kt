package com.dimmaranch.skull.phaseUI

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dimmaranch.skull.Utils
import com.dimmaranch.skull.commonUI.BidStepper
import com.dimmaranch.skull.commonUI.Theme.defaultTextStyle
import com.dimmaranch.skull.state.Card
import com.dimmaranch.skull.state.GameAction
import com.dimmaranch.skull.state.GameState
import com.dimmaranch.skull.state.hasRose
import com.dimmaranch.skull.state.hasSkull
import com.dimmaranch.skull.state.isCurrentUserPlayer
import com.dimmaranch.skull.viewmodel.GameViewModel
import org.jetbrains.compose.resources.painterResource
import skull.composeapp.generated.resources.Res
import skull.composeapp.generated.resources.blueback
import skull.composeapp.generated.resources.blueskull

@Composable
fun PlacingPhaseUI(viewModel: GameViewModel, isPlacingFirstCard: Boolean = false) {
    val gameState: GameState by viewModel.gameState.collectAsState()
    val optionClicked = remember { mutableStateOf(false) }
    val players = gameState.players.values.toList()
    val isCurrentTurn = gameState.isCurrentUserPlayer(viewModel.getCurrentUserId().orEmpty())

    if (!isPlacingFirstCard) {
        optionClicked.value = false
    }
    val buttonsEnabled = if (isPlacingFirstCard) {
        !optionClicked.value
    } else {
        !optionClicked.value && isCurrentTurn
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isPlacingFirstCard) {
            // Can even put the names of the players you are waiting on here
            Text(
                text = if (optionClicked.value) "Waiting for other players to place cards..." else "Place a Rose or Skull to get the round started!",
                textAlign = TextAlign.Center,
                style = defaultTextStyle,
                color = Color.Yellow,
            )
        } else {
            Text(
                text = "Current Player: ${players[gameState.currentPlayerIndex].name}",
                style = defaultTextStyle
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row {
            Button(
                onClick = {
                    viewModel.handleAction(GameAction.PlaceCard(Card.ROSE, isPlacingFirstCard))
                    optionClicked.value = true
                },
                modifier = Modifier.padding(8.dp),
                enabled = buttonsEnabled && players[gameState.currentPlayerIndex].cardsInHand.hasRose()
            ) {
                Text("Play Rose")
            }

            Button(
                onClick = {
                    viewModel.handleAction(
                        GameAction.PlaceCard(
                            Card.SKULL,
                            isPlacingFirstCard
                        )
                    )
                    optionClicked.value = true
                },
                modifier = Modifier.padding(8.dp),
                enabled = buttonsEnabled && players[gameState.currentPlayerIndex].cardsInHand.hasSkull()
            ) {
                Text("Play Skull")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (optionClicked.value) {
            Text(
                text = "You have placed a card",
                textAlign = TextAlign.Center,
                style = defaultTextStyle
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        if (isPlacingFirstCard) {
            Text(
                text = "You can bid or place more cards once it is your turn",
                textAlign = TextAlign.Center,
                style = defaultTextStyle
            )
        } else {
            BidStepper(
                currentBid = players[gameState.currentPlayerIndex].bid,
                cardsList = gameState.placedCards,
                isCurrentTurn = isCurrentTurn,
                onBidChange = { newBid ->
                    viewModel.handleAction(GameAction.PlaceBid(newBid))
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        players.find { it.id == viewModel.getCurrentUserId() }?.let { userPlayer ->
            val playerIndex = players.indexOf(userPlayer)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (userPlayer.cardsInHand.size > 0) CardImageWithText(userPlayer.cardsInHand[0], playerIndex)
                    Spacer(modifier = Modifier.height(16.dp))
                    if (userPlayer.cardsInHand.size > 1) CardImageWithText(userPlayer.cardsInHand[1], playerIndex)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (userPlayer.cardsInHand.size > 2) CardImageWithText(userPlayer.cardsInHand[2], playerIndex)
                    Spacer(modifier = Modifier.height(16.dp))
                    if (userPlayer.cardsInHand.size > 3) CardImageWithText(userPlayer.cardsInHand[3], playerIndex)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = if ((players.firstOrNull { it.id == viewModel.getCurrentUserId() }?.points
                    ?: 0) > 0
            ) "You have 1 point. You need 1 more to win!" else "You have 0 points. Get 2 to win.",
            style = defaultTextStyle,
        )
    }
}

@Composable
fun CardImageWithText(
    card: Card,
    playerIndex: Int = 0
) {
    Text(
        text = card.name,
        style = defaultTextStyle,
    )
    val image = Utils.mapPlayerIndexToDrawable(playerIndex, card == Card.SKULL, card == Card.ROSE)
    Image(
        painter = painterResource(image),
        contentDescription = null,
        modifier = Modifier.size(64.dp)
    )
}