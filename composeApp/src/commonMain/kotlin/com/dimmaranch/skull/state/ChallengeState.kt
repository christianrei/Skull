package com.dimmaranch.skull.state

import com.dimmaranch.skull.state.Card
import kotlinx.serialization.Serializable

@Serializable
data class ChallengeState(
    val challengerId: String = "",
    val revealedCards: List<RevealedCard> = emptyList(),
)