package com.dimmaranch.skull.network

import com.dimmaranch.skull.state.Player
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.database.FirebaseDatabase
import dev.gitlive.firebase.database.database
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.storage.storage
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull


class GameRepository(private val database: FirebaseDatabase) {

    private val _gameState = MutableStateFlow<Map<String, Any>?>(null)
    val gameState: StateFlow<Map<String, Any>?> get() = _gameState

    /**
     * Creates a new game room with a unique game ID.
     */
    suspend fun createGameRoom(gameId: String, hostPlayerId: String) {
        val userId = getCurrentUserId().orEmpty()
        val initialGameState = mapOf(
            "host" to hostPlayerId,
            "players" to mapOf(userId to Player(id = userId, name = hostPlayerId)),
        )
        database.reference("gameRooms/$gameId").setValue(initialGameState)
    }

    /**
     * Joins an existing game room.
     */
    suspend fun joinGameRoom(gameId: String, player: Player): JoinGameResult {
        val gameRef = database.reference("gameRooms/$gameId")

        return try {
            // Check if the game exists
            val snapshot = gameRef.valueEvents.firstOrNull()
            if (snapshot?.exists == false) return JoinGameResult.GameNotFound // Game room doesn't exist

            val playersSnapshot = snapshot?.child("players")
            val currentPlayerCount = playersSnapshot?.children?.count() ?: 0
            if (currentPlayerCount >= 6) return JoinGameResult.RoomFull

            gameRef.child("players").child(player.id).setValue(player)
            JoinGameResult.Success
        } catch (e: Exception) {
            JoinGameResult.UnknownError(e.toString())
        }
    }

    /**
     * Deletes the game data from the database 12 hours after game start.
     */
    suspend fun deleteGameData(gameId: String) {
        val database = Firebase.database
        val gameRef = database.reference("games/$gameId")

        // Delete the game data after the game is finished
        gameRef.removeValue()
    }

    suspend fun scheduleGameDeletion(gameId: String) {
        // Delay for 12 hours (12 * 60 * 60 * 1000 milliseconds)
        delay(12 * 60 * 60 * 1000L)

        // Perform game data deletion after 12 hours
        deleteGameData(gameId)
    }

    /**
     * Updates the game state (e.g., after a move is made).
     */
    suspend fun updateGameState(gameId: String, newState: Map<String, Any?>) {
        database.reference("gameRooms/$gameId").updateChildren(newState)
    }

    suspend fun uploadImage(userId: String, uri: ByteArray, type: String): String {
        val storage = Firebase.storage
        val ref = storage.reference.child("user_images/$userId/$type.jpg")

//        val byteArrayInputStream = ByteArrayInputStream(uri)
//        val file = File("NSURL")
//        uri.inputStream().use { inputStream ->
//            ref.putBytesResumable(inputStream)
//        }
//
//        val uploadTask = ref.putFileResumable(uri)//.await()
        return ref.getDownloadUrl() // Needs await?
    }

    suspend fun saveUserImageUrl(userId: String, imageUrl: String, type: String) {
        val db = Firebase.firestore
        db.collection("users").document(userId).update(type)
        db.collection("users").document(userId).update(imageUrl)//.await()
    }

    suspend fun getUserImages(userId: String): Pair<String?, String?> {
        val db = Firebase.firestore
        val doc = db.collection("users").document(userId).get()//.await()
        return Pair(doc.get("skull"), doc.get("rose"))
    }

    fun getCurrentUserId(): String? {
        val auth = Firebase.auth
        return auth.currentUser?.uid//?.replace("[^A-Za-z0-9 ]", "")
    }

    suspend fun signInAnon() {
        val auth = Firebase.auth
        if (auth.currentUser == null) {
            auth.signInAnonymously()
        }
    }
}

