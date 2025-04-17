package com.dimmaranch.skulls.network

sealed class JoinGameResult {
    object Success : JoinGameResult()
    object GameNotFound : JoinGameResult()
    object RoomFull : JoinGameResult()
    data class UnknownError(val message: String) : JoinGameResult()
}