package com.dimmaranch.skulls.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dimmaranch.skull.viewmodel.GameViewModel
import com.dimmaranch.skulls.commonUI.Theme.SecondaryText
import com.dimmaranch.skulls.commonUI.Theme.defaultTextStyle
import com.dimmaranch.skulls.state.Player

@Composable
fun CreateRoomScreen(
    viewModel: GameViewModel,
    roomCode: String,
    roomHostId: String?,
    isHost: Boolean = false,
    players: List<Player>,
    onStartGame: () -> Unit
) {
    var isStartEnabled by remember { mutableStateOf(false) }

    LaunchedEffect(players.size) {
        isStartEnabled = players.size >= 2 && isHost
    }

    LaunchedEffect(roomCode) {
        viewModel.observeGameState()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row {
            Text(
                text = "Room Host: ",
                style = defaultTextStyle
            )
            Text(
                text = roomHostId ?: "WHO?", //get host with updated gameRoom
                style = defaultTextStyle.copy(color = SecondaryText)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row {
            Text(
                text = "Room Code: ",
                style = defaultTextStyle.copy(fontSize = 32.sp)
            )
            Text(
                text = roomCode,
                style = defaultTextStyle.copy(fontSize = 32.sp, color = SecondaryText)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Players in the Room:",
            style = defaultTextStyle
        )

        Spacer(modifier = Modifier.height(8.dp))

        players.forEach {
            Text(
                text = it.name,
                style = defaultTextStyle.copy(color = SecondaryText)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onStartGame,
            enabled = isStartEnabled
        ) {
            Text(text = "Start Game")
        }
    }
}
