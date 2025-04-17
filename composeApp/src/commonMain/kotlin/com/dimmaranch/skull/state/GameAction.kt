package com.dimmaranch.skulls.state

sealed class GameAction {
    data class PlaceCard(val card: Card, val isPlacingFirstCard: Boolean) : GameAction()
    data class PlaceBid(val bid: Int) : GameAction()
    data class Challenge(val playerIndex: Int) : GameAction()
    data object StartGame : GameAction()
    data object StartBidding : GameAction()
    data class RevealNextCard(val playerId: String, val cardIndex: Int) : GameAction()
    data class LoseCard(val playerId: String, val cardIndex: Int) : GameAction()
    data object PassTurn : GameAction()
    data object EndTurn : GameAction()
}