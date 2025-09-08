package com.dimmaranch.skull.viewmodel

import com.dimmaranch.skull.Utils
import com.dimmaranch.skull.Utils.generateRandomCode
import com.dimmaranch.skull.Utils.toMap
import com.dimmaranch.skull.Utils.toPlayerMap
import com.dimmaranch.skull.network.GameRepository
import com.dimmaranch.skull.network.JoinGameResult
import com.dimmaranch.skull.state.Card
import com.dimmaranch.skull.state.ChallengeState
import com.dimmaranch.skull.state.GameAction
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
import com.russhwolf.settings.*

class GameViewModel {
    private val logger = KotlinLogging.logger {}
    private val database: FirebaseDatabase =
        Firebase.database("https://skulls-d7a17-default-rtdb.firebaseio.com/")

    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val repository = GameRepository(database)

    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState

    private val _userNameState =
        MutableStateFlow("")//"" for Release, use generateRandomCode() for testing
    val userNameState: StateFlow<String> = _userNameState

    private val settings: Settings = Settings()
    private val LAST_GAME_ID_KEY = "last_game_id"

    fun shouldRejoinGame(): Boolean {
        val lastGameId = settings.getStringOrNull(LAST_GAME_ID_KEY)
        if (lastGameId != null) {
            println("Rejoining last game: $lastGameId")
            observeGameState(lastGameId)
        }
        return lastGameId != null
    }

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

                GameAction.StartGame -> currentState.copy(
                    phase = Phase.PLACING_FIRST_CARD,
                )

                GameAction.StartBidding -> startBidding()

                is GameAction.RevealNextCard -> {
                    revealCard(action.playerId, action.cardIndex)
                }

                is GameAction.RevealAnimationDone -> {
                    revealAnimationDone()
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
                    cardsInHand = Utils.buildHand(),
                ))
            )
        }
        coroutineScope.launch {
            settings.putString(LAST_GAME_ID_KEY, _gameState.value.roomCode) // Save for recovery
            repository.updateGameState(_gameState.value.roomCode, _gameState.value.toMap())
        }
    }

    fun joinGameRoom(gameCode: String) {
        settings.putString(LAST_GAME_ID_KEY, gameCode) // Save for recovery

        // Create a player object with an id along with the name and add it to the room
        val playerId = _userNameState.value
        coroutineScope.launch {
            val result = repository.joinGameRoom(
                gameCode,
                Player(
                    id = getCurrentUserId() ?: UUID.randomUUID().toString(),
                    name = playerId,
                    cardsInHand = Utils.buildHand(),
                )
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
                                cardsInHand = Utils.buildHand(),
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

    private fun updatePlayersAfterTurn(card: Card): List<Player>? {
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
            )
        } else {
            gameState
        }
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
        val nextPhase = if (updatedPlayers.size == 1) getPhaseEnd() else Phase.PLACING_FIRST_CARD

        return currentState.copy(players = updatedPlayers.toPlayerMap(), phase = nextPhase)
    }

    private fun getPhaseEnd(): Phase {
        settings.remove(LAST_GAME_ID_KEY)
        return Phase.END
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
            player.cardsInHand.addAll(returnedCards)
            player.copy(
                cardsInHand = player.cardsInHand,
                bid = 0,
                hasPassedTurn = false
            )
        }.toPlayerMap()
    }

    fun clearGame() {
        coroutineScope.launch {
            settings.remove(LAST_GAME_ID_KEY)
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

    private fun revealCard(playerId: String, cardIndex: Int): GameState {
        val game = gameState.value
        val challenge = game.challengeState ?: return game
        val players = game.players.values.toList()

        if (challenge.revealedCards.size >= (game.highestBid ?: 0)) return game

        val playerCards = game.placedCards[playerId] ?: return game
        if (cardIndex !in playerCards.indices) return game

        val revealedCard = playerCards[cardIndex]
        val updatedRevealedCards = challenge.revealedCards + RevealedCard(revealedCard, playerId)

        val revealed = game.placedCards[playerId]?.map {
            RevealedCard(it, playerId)
        }.orEmpty()

        val placed = game.placedCards[playerId].orEmpty()
        val lastCard = placed.lastOrNull() ?: return game
        val updatedPlaced = placed.dropLast(1)

        val currentPlaced = game.placedCards.toMutableMap()
        currentPlaced[playerId] = updatedPlaced

        val player =
            players.find { it.id == playerId } ?: return game // fallback in case ID not found
        val currentHand = player.cardsInHand.toMutableList()

        val updatedPlayers = players.map {
            if (it.id == playerId) {
                currentHand.add(lastCard)
                it.copy(cardsInHand = currentHand) // append to hand
            } else it
        }.toPlayerMap()

        return game.copy(
            remainingCardsToReveal = game.remainingCardsToReveal - 1,
            challengedPlayerIndex = players.indexOf(players.find { it.id == playerId }),
            revealedCards = revealed,
            placedCards = currentPlaced,
            players = updatedPlayers,
            challengeState = challenge.copy(revealedCards = updatedRevealedCards),
//            lastRevealedCard = revealedCard // 👈 optional if you want to animate specific card
        )
    }

    private fun revealAnimationDone(): GameState {
        val game = gameState.value
        val challenge = game.challengeState ?: return game

        val lastCard = challenge.revealedCards.lastOrNull()

        if (lastCard?.card == Card.SKULL) {
            // Skull revealed
            return game.copy(
                phase = Phase.LOSE_A_CARD,
                players = returnPlacedCardsToPlayersAndResetBid(game),
//                            challengedPlayerIndex = game.challengedPlayerIndex,
                remainingCardsToReveal = 0,
                placedCards = emptyMap(),
                revealedCards = emptyList(),
                challengeState = challenge,
                currentPlayerIndex = game.challengedPlayerIndex,
            )
        } else if (challenge.revealedCards.size == game.highestBid) {
            // All roses revealed
            val updatedPlayers = game.players.values.map {
                if (it.id == challenge.challengerId) {
                    if (it.points == 1) return game.copy(phase = getPhaseEnd())
                    it.copy(points = it.points + 1)
                } else it
            }.toPlayerMap()

            return game.copy(
                phase = Phase.PLACING_FIRST_CARD,
                remainingCardsToReveal = 0,
                players = returnPlacedCardsToPlayersAndResetBid(game.copy(players = updatedPlayers)),
                currentPlayerIndex = game.currentBidderIndex,
                challengeState = null,
                revealedCards = emptyList(),
                placedCards = emptyMap(),
            )
        } else {
            // More cards to reveal, no phase change yet
            return game.copy(
                revealedCards = emptyList()
            )
        }
    }

}