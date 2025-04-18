package com.dimmaranch.skull

import com.dimmaranch.skull.state.Card
import com.dimmaranch.skull.state.GameState
import com.dimmaranch.skull.state.Phase
import com.dimmaranch.skull.state.Player
import org.jetbrains.compose.resources.DrawableResource
import skull.composeapp.generated.resources.Res
import skull.composeapp.generated.resources.blueback
import skull.composeapp.generated.resources.blueskull
import skull.composeapp.generated.resources.brownback
import skull.composeapp.generated.resources.brownrose
import skull.composeapp.generated.resources.brownskull
import skull.composeapp.generated.resources.greenback
import skull.composeapp.generated.resources.greenrose
import skull.composeapp.generated.resources.greenskull
import skull.composeapp.generated.resources.pinkback
import skull.composeapp.generated.resources.pinkrose
import skull.composeapp.generated.resources.pinkskull
import skull.composeapp.generated.resources.redback
import skull.composeapp.generated.resources.redskull
import skull.composeapp.generated.resources.resrose
import skull.composeapp.generated.resources.yellowback
import skull.composeapp.generated.resources.yellowrose
import skull.composeapp.generated.resources.yellowskull
import kotlin.math.PI

object Utils {

    const val NAME_MAX_LENGTH = 12

    fun isPlayerNameValid(playerName: String): Boolean {
        return playerName.length in 3..NAME_MAX_LENGTH
    }

    fun isRoomCodeValid(roomCode: String): Boolean {
        return roomCode.length == 4
    }

//    fun GameState.toMap(): Map<String, Any> {
//        return Json.encodeToJsonElement(this).jsonObject.mapValues { it.value.toString() }
//    }

    fun GameState.toMap(): Map<String, Any?> {
        return mapOf(
            // Room values
            "roomCode" to roomCode,
            "hostId" to hostId,
            "doesRoomExist" to canJoinRoom,
            "showNoRoomMessage" to noRoomMessage,

            // Game values
            "players" to players,
            "currentPlayerIndex" to currentPlayerIndex,
            "currentBidderIndex" to currentBidderIndex,
            "challengerId" to challengerId,
            "challengedPlayerIndex" to challengedPlayerIndex,
            "currentBid" to currentBid,
            "totalBids" to totalBids,
            "highestBid" to highestBid,
            "placedCards" to placedCards,
            "phase" to phase,
            "challengeState" to challengeState,
            "revealedCards" to revealedCards,
            "remainingCardsToReveal" to remainingCardsToReveal
        )
    }

    fun Map<String, Any>.toGameState(): GameState {
        val gameState = GameState(
            roomCode = this["roomCode"] as String,
            hostId = this["hostId"] as String,
            canJoinRoom = this["doesRoomExist"] as Boolean,
            noRoomMessage = this["showNoRoomMessage"] as String,
            currentPlayerIndex = (this["currentPlayerIndex"] as? Int) ?: 0,
            currentBidderIndex = (this["currentBidderIndex"] as? Int) ?: 0,
            challengedPlayerIndex = (this["challengedPlayerIndex"] as? Int) ?: 0,
            currentBid = (this["currentBid"] as? Int) ?: 0,
            totalBids = (this["totalBids"] as? Int) ?: 0,
            highestBid = (this["highestBid"] as? Int) ?: 0,
            players = mapOf("player1" to Player(name = "cock")),
            //players = (this["players"] as? Map<String, Any>){ it.toPlayer() } ?: emptyList<Player>(),
            phase = Phase.valueOf((this["currentPhase"] as? String) ?: ""),
            remainingCardsToReveal = (this["remainingCardsToReveal"] as? Int) ?: 0
        )   // Deserialize into GameState
        return gameState
    }

    fun Player.toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "name" to name,
            "cards" to cardsInHand,
            "bid" to bid
        )
    }

    fun List<Player>.toPlayerMap(): Map<String, Player> {
        return this.associateBy { it.id }
    }

    fun Map<String, Any>.toPlayer(): Player {
        return Player(
            id = this["id"] as String,
            name = this["name"] as String,
            cardsInHand = this["cardsInHand"] as MutableList<Card>, // to enum Card
        )
    }

    fun buildHand(): MutableList<Card> {
        return mutableListOf(
            Card.ROSE,
            Card.ROSE,
            Card.ROSE,
            Card.SKULL,
        )
    }

    fun generateRandomCode(): String {
        val characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..4)
            .map { characters.random() }
            .joinToString("")
    }

    fun degreesToRadians(degrees: Double): Double {
        return degrees * (PI / 180.0)
    }

    fun mapPlayerIndexToDrawable(playerIndex: Int, getSkull: Boolean = false, getRose: Boolean = false): DrawableResource {
        return when(playerIndex) {
            0 -> if (getSkull) { Res.drawable.blueskull } else if (getRose) { Res.drawable.blueback } else { Res.drawable.blueback }
            1 -> if (getSkull) { Res.drawable.redskull } else if (getRose) { Res.drawable.resrose } else { Res.drawable.redback }
            2 -> if (getSkull) { Res.drawable.greenskull } else if (getRose) { Res.drawable.greenrose } else { Res.drawable.greenback }
            3 -> if (getSkull) { Res.drawable.brownskull } else if (getRose) { Res.drawable.brownrose } else { Res.drawable.brownback }
            4 -> if (getSkull) { Res.drawable.pinkskull } else if (getRose) { Res.drawable.pinkrose } else { Res.drawable.pinkback }
            5 -> if (getSkull) { Res.drawable.yellowskull } else if (getRose) { Res.drawable.yellowrose } else { Res.drawable.yellowback }
            else -> Res.drawable.blueback
        }
    }

}