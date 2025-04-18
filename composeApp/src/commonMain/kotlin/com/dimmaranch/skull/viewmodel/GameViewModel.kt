package com.dimmaranch.skull.viewmodel

import com.dimmaranch.skull.state.GameAction
import com.dimmaranch.skull.network.GameRepository
import com.dimmaranch.skull.network.JoinGameResult
import com.dimmaranch.skull.Utils.generateRandomCode
import com.dimmaranch.skull.Utils.toMap
import com.dimmaranch.skull.Utils.toPlayerMap
import com.dimmaranch.skull.state.Card
import com.dimmaranch.skull.state.ChallengeState
import com.dimmaranch.skull.state.GameState
import com.dimmaranch.skull.state.Phase
import com.dimmaranch.skull.state.Player
import com.dimmaranch.skull.state.PlayerAction
import com.dimmaranch.skull.state.RevealedCard
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.database.FirebaseDatabase
import dev.gitlive.firebase.database.database
import korlibs.io.util.UUID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mu.KotlinLogging

class GameViewModel {
    private val logger = KotlinLogging.logger {}
    private val database: FirebaseDatabase =
        Firebase.database("https://skulls-d7a17-default-rtdb.firebaseio.com/")

    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val repository = GameRepository(database)

    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState

    private val _userNameState =
        MutableStateFlow(generateRandomCode())//TODO PUT BACK TO "" AFTER TESTING
    val userNameState: StateFlow<String> = _userNameState

    fun handleAction(action: GameAction) {
        _gameState.update { currentState ->
            when (action) {
                is GameAction.PlaceCard -> placeCard(
                    action.card,
                    action.isPlacingFirstCard
                )

                is GameAction.PlaceBid -> takeTurn(
                    PlayerAction.START_BIDDING,
                    action.bid
                )

                is GameAction.Challenge -> currentState.copy(
                    phase = Phase.CHALLENGING,
                    remainingCardsToReveal = action.playerIndex // This should reflect how many cards need to be revealed
                )

                GameAction.StartGame -> currentState.copy(
                    phase = Phase.PLACING_FIRST_CARD,
                )

                GameAction.StartBidding -> startBidding()

                is GameAction.RevealNextCard -> {
                    revealCard(action.playerId, action.cardIndex)
                }

                is GameAction.LoseCard -> {
                    removeCardFromHand(action.playerId, action.cardIndex)
                }

                GameAction.PassTurn -> {
                    // if everyone has passed start challenge
                    passTurn()
                }

                GameAction.EndTurn -> {
                    currentState.copy(
                        currentPlayerIndex = getNextPlayerIndex(),
                        phase = Phase.PLACING_FIRST_CARD
                    )
                }
            }
        }
        updateGameState()
    }

    private fun getNextPlayerIndex(): Int {
        return (gameState.value.currentPlayerIndex + 1) % gameState.value.players.size
    }

    private fun placeCard(card: Card, isPlacingFirstCard: Boolean): GameState {
        return if (isPlacingFirstCard) {
            placeFirstCard(card)
        } else {
            takeTurn(
                action = if (card == Card.ROSE) PlayerAction.PLACE_ROSE else PlayerAction.PLACE_SKULL,
                bidAmount = null
            )
        }
    }

    // You can modify other actions to update challengedPlayer and remainingCardsToReveal accordingly
    private fun startBidding(): GameState {
        // Reset all players' bids
        val players = _gameState.value.players.values.toList()
        players.forEach { it.bid = 0 }
        return _gameState.value.copy(phase = Phase.BIDDING, highestBid = 0)
    }

    fun setPlayerName(playerName: String) {
        _userNameState.update {
            playerName
        }
    }

    fun createGameRoom(hostPlayerId: String) {
        val code = generateRandomCode()
        setPlayerName(hostPlayerId)
        _gameState.update { currentState ->
            currentState.copy(
                roomCode = code,
                canJoinRoom = true,
                noRoomMessage = null,
                hostId = hostPlayerId,
                players = currentState.players + (getCurrentUserId().orEmpty() to Player(
                    id = getCurrentUserId().orEmpty(),
                    name = hostPlayerId,
                ))
            )
        }
        coroutineScope.launch {
            repository.updateGameState(_gameState.value.roomCode, _gameState.value.toMap())
//            repository.createGameRoom(code, hostPlayerId)
        }
    }

    fun joinGameRoom(gameCode: String) {
        // Create a player object with an id along with the name and add it to the room
        val playerId = _userNameState.value
        coroutineScope.launch {
            val result = repository.joinGameRoom(
                gameCode,
                Player(id = getCurrentUserId() ?: UUID.randomUUID().toString(), name = playerId)
            )
            if (result == JoinGameResult.Success) {
                CoroutineScope(Dispatchers.Main).launch {
                    _gameState.update { currentState ->
                        currentState.copy(
                            roomCode = gameCode,
                            canJoinRoom = true,
                            players = currentState.players + (getCurrentUserId().orEmpty() to Player(
                                id = getCurrentUserId().orEmpty(),
                                name = playerId,
                            ))
                        )
                    }
                }
            } else {
                println("Failed to join room: $gameCode")
                _gameState.update { currentState ->
                    currentState.copy(
                        roomCode = "",
                        canJoinRoom = false,
                        noRoomMessage = if (result == JoinGameResult.RoomFull) "Sorry, the room already has 6 players!" else "The room does not exist!",
                    )
                }
            }
        }
    }

    fun updateRoomCodeInput() {
        _gameState.update { currentState ->
            currentState.copy(canJoinRoom = false, noRoomMessage = null)
        }
    }

    fun observeGameState() {
        observeGameState(gameState.value.roomCode)
    }

    private fun updateGameState() {
        coroutineScope.launch {
            repository.updateGameState(_gameState.value.roomCode, _gameState.value.toMap())
        }
    }

    fun uploadAndSaveImage(userId: String, uri: ByteArray, type: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val imageUrl = repository.uploadImage(userId, uri, type)
                repository.saveUserImageUrl(userId, imageUrl, type)
            } catch (e: Exception) {
                logger.error(e) { "Error uploading $type image" }
            }
        }
    }

    /**
     * Observes game state updates in real-time.
     */
    private fun observeGameState(roomCode: String) {
        coroutineScope.launch(Dispatchers.IO) {
            database.reference("gameRooms/$roomCode").valueEvents.collect { snapshot ->
                if (snapshot.exists) {
                    //println("MEME: gamestatedebug: " + snapshot.value.toString())
                    snapshot.value(GameState.serializer())?.let { newGameState ->
                        withContext(Dispatchers.Main) {
                            _gameState.update { _ -> newGameState }
                        }
                    }
                }
            }
        }
    }

    private fun placeFirstCard(card: Card): GameState {
        val playerId = getCurrentUserId().orEmpty()
        // Ensure the player has not already placed their first card
        if (gameState.value.placedCards[playerId]?.isNotEmpty() == true) return _gameState.value

        // Add the player's first card
        val updatedCards = gameState.value.placedCards.toMutableMap()
        updatedCards[playerId] = listOf(card)

        // Check if all players placed their first card
        val allPlaced = gameState.value.players.values.toList()
            .all { updatedCards[it.id]?.isNotEmpty() == true }

        val updatedPhase = if (allPlaced) {
            Phase.PLACING.name
        } else {
            Phase.PLACING_FIRST_CARD.name
        }
        return gameState.value.copy(
            players = updatePlayersAfterTurn(card).orEmpty().toPlayerMap(),
            placedCards = updatedCards,
            phase = Phase.valueOf(updatedPhase)
        )
    }

    fun updatePlayersAfterTurn(card: Card): List<Player>? {
        val player = gameState.value.players.values.toList().find {
            it.id == getCurrentUserId()
        }
        player?.let { matchedPlayer ->
            val updatedCards = mutableListOf<Card>()
            updatedCards.addAll(matchedPlayer.cardsInHand)
            updatedCards.remove(updatedCards.find { it.name == card.name })
            matchedPlayer.cardsInHand.clear()
            matchedPlayer.cardsInHand.addAll(updatedCards)
            // Update the game state with the new cards by changing player
            return gameState.value.players.values.toList()
        }
        return null
    }

    private fun takeTurn(
        action: PlayerAction,
        bidAmount: Int? = null
    ): GameState {
        val playerId = getCurrentUserId().orEmpty()
        when (action) {
            PlayerAction.PLACE_ROSE, PlayerAction.PLACE_SKULL -> {
                val cardToPlace = if (action == PlayerAction.PLACE_ROSE) Card.ROSE else Card.SKULL
                val updatedCards = gameState.value.placedCards.toMutableMap()
                updatedCards[playerId] =
                    updatedCards[playerId].orEmpty() + cardToPlace // Add a new card

                val updatedPlayers = updatePlayersAfterTurn(cardToPlace)
                val allPlayersPlacedAllCards = updatedPlayers?.all { it.cardsInHand.isEmpty() }
                val nextPhase =
                    if (allPlayersPlacedAllCards == true) Phase.BIDDING else gameState.value.phase
                return gameState.value.copy(
                    players = updatedPlayers.orEmpty().toPlayerMap(),
                    placedCards = updatedCards,
                    phase = nextPhase,
                    currentPlayerIndex = getNextPlayerIndex(),
                )
            }

            PlayerAction.START_BIDDING -> {
//                if (gameState.value.players.values.toList()[gameState.value.currentBidderIndex].id != playerId) return gameState.value

                val minBid = 1
                val maxBid = gameState.value.placedCards.values.sumOf { it.size }

                if (bidAmount == null || bidAmount !in minBid..maxBid) return gameState.value

                // Update the specific player's bid
                val updatedPlayers = gameState.value.players.mapValues { (id, player) ->
                    if (id == playerId) {
                        player.copy(bid = bidAmount) // Assuming `bid` is a property in your Player class
                    } else {
                        player
                    }
                }

                val updatedGameState = gameState.value.copy(
                    phase = Phase.BIDDING,
                    highestBid = bidAmount,
                    players = updatedPlayers,
                    currentBidderIndex = gameState.value.currentPlayerIndex,
                    currentPlayerIndex = getNextPlayerIndex(),
                )
                return checkForChallengeStart(updatedGameState)
            }
        }
    }

    private fun checkForChallengeStart(gameState: GameState): GameState {
        val totalCardsPlaced = gameState.placedCards.values.sumOf { it.size }
        val currentBid = gameState.highestBid ?: 0
        val players = gameState.players.values.toList()
        val activeBidders = players.count { it.hasPassedTurn }

        return if (currentBid >= totalCardsPlaced || activeBidders == 1) { // Should we have some kind of reset all method?
            // Reset all players' bidding status
            val updatedPlayers = players.map { it.copy(hasPassedTurn = false) }.toPlayerMap()

            // Start the challenge phase when the bid reaches the total cards or only one bidder remains
            val currentPlayer = players[gameState.currentBidderIndex]

            val revealed = gameState.placedCards[currentPlayer.id]?.map { card ->
                RevealedCard(card = card, playerId = currentPlayer.id)
            }.orEmpty()

            gameState.copy(
                players = updatedPlayers,
                phase = Phase.CHALLENGING,
                challengerId = players[gameState.currentBidderIndex].id,
                challengeState = ChallengeState(
                    challengerId = currentPlayer.id,
                    revealedCards = emptyList()
                ),
                remainingCardsToReveal = gameState.highestBid ?: 0,
                currentPlayerIndex = gameState.currentBidderIndex,
                revealedCards = revealed
            )
        } else {
            gameState
        }
    }

    private fun revealCard(playerId: String, cardIndex: Int): GameState {
        val game = gameState.value
        val challenge = game.challengeState ?: return game
        val players = game.players.values.toList()
        val currentPlayer = players.firstOrNull { it.id == challenge.challengerId } ?: return game

        // Prevent revealing more cards than the bid
        if (challenge.revealedCards.size >= (game.highestBid ?: 0)) return game

        val playerCards = game.placedCards[playerId] ?: return game
        if (cardIndex !in playerCards.indices) return game

        val revealedCard = playerCards[cardIndex]
        val updatedRevealedCards = challenge.revealedCards + revealedCard

        // If the player revealed a Skull, they must lose a card
        if (revealedCard == Card.SKULL) {
            println("MEME: SKULL REVEALED")
            return game.copy(
                phase = Phase.LOSE_A_CARD,
                players = returnPlacedCardsToPlayersAndResetBid(game),
                challengedPlayerIndex = players.indexOf(currentPlayer),
                remainingCardsToReveal = 0,
                placedCards = emptyMap(),
                currentPlayerIndex = players.indexOf(currentPlayer), //TODO Switch to bidder to mix up and mark done then challenged
                challengeState = challenge.copy(revealedCards = updatedRevealedCards)
            )
        }

        // If they revealed all required Roses, they get a point
        if (updatedRevealedCards.size == game.highestBid) {
            println("MEME: ALL ROSES REVEALED")
            val updatedPlayers = players.map { player ->
                if (player.id == currentPlayer.id) {
                    // If already have 1 point, this second point makes them win
                    if (player.points == 1) {
                        return game.copy(
                            phase = Phase.END,
                        )
                    }
                    player.copy(points = player.points + 1)
                } else {
                    player
                }
            }.toPlayerMap()

            return game.copy(
                phase = Phase.PLACING_FIRST_CARD,
                remainingCardsToReveal = 0,
                challengedPlayerIndex = players.indexOf(currentPlayer),
                players = returnPlacedCardsToPlayersAndResetBid(game.copy(players = updatedPlayers)),
                currentPlayerIndex = game.currentBidderIndex,
//                currentBidderIndex = 0,
                challengeState = null,
                revealedCards = emptyList(),
                placedCards = emptyMap()
            )
        }

        return game.copy(
            remainingCardsToReveal = game.remainingCardsToReveal - 1,
            currentPlayerIndex = players.indexOf(currentPlayer),
            challengeState = challenge.copy(revealedCards = updatedRevealedCards)
        )
    }

    private fun removeCardFromHand(playerId: String, cardIndex: Int): GameState {
        val currentState = gameState.value
        val updatedPlayers = currentState.players.values.toMutableList()

        // Find the player
        val playerIndex = updatedPlayers.indexOfFirst { it.id == playerId }
        if (playerIndex == -1) return currentState // Player not found

        val player = updatedPlayers[playerIndex]

        // Check if the index is valid
        if (cardIndex !in player.cardsInHand.indices) return currentState

        // Remove the selected card
        val updatedHand = player.cardsInHand.toMutableList().apply { removeAt(cardIndex) }

        if (updatedHand.isEmpty()) {
            // Player is eliminated, remove them from the game
            updatedPlayers.removeAt(playerIndex)
        } else {
            // Update the player with the new hand
            updatedPlayers[playerIndex] = player.copy(cardsInHand = updatedHand)
        }

        // Check if only one player remains → Declare winner
        val nextPhase = if (updatedPlayers.size == 1) Phase.END else Phase.PLACING_FIRST_CARD

        return currentState.copy(players = updatedPlayers.toPlayerMap(), phase = nextPhase)
    }

    private fun passTurn(): GameState {
        val currentState = gameState.value
        val players = currentState.players

        // Mark current player as passed
        val updatedPlayers = players.mapValues { (id, player) ->
            if (player.id == getCurrentUserId()) {
                player.copy(hasPassedTurn = true)
            } else player
        }

//        val highestBid = currentState.highestBid ?: 0
        // Get the highest bid (excluding passed players)
        val highestBid = updatedPlayers.maxByOrNull { it.value.bid }?.value?.bid ?: 0
        // Find player(s) with that bid
        val highestBidders = updatedPlayers.filterValues { it.bid == highestBid }

        // Check if everyone else has passed
        var passedCount = 0
        updatedPlayers.values.forEach {
            if (it.hasPassedTurn) passedCount++
        }
        println("MEME: passedCount: $passedCount")
        val allOthersPassed = passedCount == updatedPlayers.keys.size - 1

        println("MEME: allOthersPassed: $allOthersPassed")
        println("MEME: highestBid: $highestBid")
        return if (allOthersPassed && highestBid > 0) {
            // Transition to challenge phase
            currentState.copy(
                phase = Phase.CHALLENGING,
                challengerId = highestBidders.keys.first(),
                challengeState = ChallengeState(
                    challengerId = highestBidders.keys.first(),
                    revealedCards = emptyList()
                ),
                players = updatedPlayers,
                remainingCardsToReveal = highestBid,
                currentPlayerIndex = players.keys.indexOf(highestBidders.keys.first()) // or however you want to assign it
            )
        } else {
            // Stay in bidding phase, move to next player
            currentState.copy(
                players = updatedPlayers,
                currentPlayerIndex = getNextPlayerIndex()
            )
        }
    }

    private fun returnPlacedCardsToPlayersAndResetBid(gameState: GameState): Map<String, Player> {
        return gameState.players.values.toList().map { player ->
            val returnedCards = gameState.placedCards[player.id].orEmpty()
            player.bid = 0
            player.cardsInHand.addAll(returnedCards)
            player.copy(cardsInHand = player.cardsInHand)
        }.toPlayerMap()
    }

    fun clearGame() {
        coroutineScope.launch {
            repository.deleteGameData(gameState.value.roomCode)
            _gameState.value = GameState()
        }
    }

    fun clearRevealedCards() {
        _gameState.update {
            it.copy(revealedCards = emptyList())
        }
    }

    fun getCurrentUserId(): String? {
        return repository.getCurrentUserId()
    }

    fun signInAnon() {
        coroutineScope.launch {
            repository.signInAnon()
        }
    }
}