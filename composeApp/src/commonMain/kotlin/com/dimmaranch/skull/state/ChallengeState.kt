package com.dimmaranch.skulls.state

import kotlinx.serialization.Serializable

@Serializable
data class ChallengeState(
    val challengerId: String = "",
    val revealedCards: List<Card> = emptyList(),
)