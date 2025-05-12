package com.dimmaranch.skull

import com.dimmaranch.skull.state.Card
import com.dimmaranch.skull.state.GameState
import com.dimmaranch.skull.state.Phase
import com.dimmaranch.skull.state.Player
import org.jetbrains.compose.resources.DrawableResource
import skull.composeapp.generated.resources.Res
import skull.composeapp.generated.resources.blueback
import skull.composeapp.generated.resources.bluerose
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
import skull.composeapp.generated.resources.redrose
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

    fun List<Player>.toPlayerMap(): Map<String, Player> {
        return this.associateBy { it.id }
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
            0 -> if (getSkull) { Res.drawable.blueskull } else if (getRose) { Res.drawable.bluerose } else { Res.drawable.blueback }
            1 -> if (getSkull) { Res.drawable.redskull } else if (getRose) { Res.drawable.redrose } else { Res.drawable.redback }
            2 -> if (getSkull) { Res.drawable.brownskull } else if (getRose) { Res.drawable.brownrose } else { Res.drawable.brownback }
            3 -> if (getSkull) { Res.drawable.pinkskull } else if (getRose) { Res.drawable.pinkrose } else { Res.drawable.pinkback }
            4 -> if (getSkull) { Res.drawable.yellowskull } else if (getRose) { Res.drawable.yellowrose } else { Res.drawable.yellowback }
            5 -> if (getSkull) { Res.drawable.greenskull } else if (getRose) { Res.drawable.greenrose } else { Res.drawable.greenback }
            else -> if (getSkull) { Res.drawable.blueskull } else if (getRose) { Res.drawable.bluerose } else { Res.drawable.blueback }
        }
    }

}