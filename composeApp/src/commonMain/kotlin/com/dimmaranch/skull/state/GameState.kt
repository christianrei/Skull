package com.dimmaranch.skull.state

import com.dimmaranch.skull.Utils
import com.dimmaranch.skull.viewmodel.GameViewModel
import kotlinx.serialization.Serializable

// Customizable skull and rose image
@Serializable
data class Player(
    val id: String = "",
    val name: String,
    val cardsInHand: MutableList<Card> = Utils.buildHand(),
    var bid: Int = 0,
    var points: Int = 0,
    val hasPassedTurn: Boolean = false,
    val isEliminated: Boolean = false,
    //color
    //skullImage
    //roseImage
)

@Serializable
enum class Card { SKULL, ROSE }

@Serializable
data class RevealedCard(
    val card: Card,
    val playerId: String
)

// Setup = Before the game has even started just showing the players.
// Placing First Card = At the start of every round users have to place one of their cards before they can bid.
// Placing = Players place another card if they would like or bid. Once bidding begins you can no longer place.
// Bidding = Players place their bids. They can choose to pass or bid a higher number.
// Challenging = The player who won the bid must click on other users and flip the amount of cards they bid. If they reveal only roses they receive a point.
// Lose a Card = If a player reveals a skull when challenging they must lose a card or be eliminated. No one receives a point.
// End = Show a victory or defeat screen for players. Phases 2 through 6 repeat until the game is over.
@Serializable
enum class Phase { SETUP, PLACING_FIRST_CARD, PLACING, BIDDING, CHALLENGING, LOSE_A_CARD, END }

@Serializable
enum class PlayerAction { PLACE_ROSE, PLACE_SKULL, START_BIDDING }

@Serializable
data class GameState(
    //Room values
    val roomCode: String = Utils.generateRandomCode(),//TODO PUT BACK TO FOR RELEASE"",
    val hostId: String? = null,
    val canJoinRoom: Boolean = false,
    val noRoomMessage: String? = null,
    val players: Map<String, Player> = emptyMap(),
    //Game values
    val currentPlayerIndex: Int = 0,
    val currentBidderIndex: Int = 0,
    val challengerId: String = "",
    val challengedPlayerIndex: Int = 0,
    val currentBid: Int = 0,
    val totalBids: Int = 0,
    val highestBid: Int? = null,
    val placedCards: Map<String, List<Card>> = emptyMap(), // Player ID -> List of placed cards
    val phase: Phase = Phase.SETUP,
    val challengeState: ChallengeState? = null,
    val revealedCards: List<RevealedCard> = emptyList(), // Needed to notify apps which cards to animate reveal
    val remainingCardsToReveal: Int = 0 // New: Number of cards left to reveal
)

//@Serializable
//data class GameRoom(
//    val roomCode: String = "",
//    val hostId: String? = null,
//    val userPlayerId: String = "",
//    val doesRoomExist: Boolean = false,
//    val showNoRoomMessage: Boolean = false,
//    val players: List<Player> = emptyList(),
//)

fun GameState.isUserRoomHost(userPlayerId: String): Boolean {
    return this.hostId == userPlayerId
}

fun GameState.isCurrentUserPlayer(userPlayerId: String): Boolean {
    if (this.players.isEmpty()) return false
    return this.players.values.toList()[this.currentPlayerIndex].id == userPlayerId
}

fun GameState.isCurrentUserPlayer(viewModel: GameViewModel): Boolean {
    if (this.players.isEmpty()) return false
    return this.players.values.toList()[this.currentPlayerIndex].id == viewModel.getCurrentUserId().orEmpty()
}