package com.dimmaranch.skull.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.dimmaranch.skull.Utils
import com.dimmaranch.skull.commonUI.Theme
import com.dimmaranch.skull.commonUI.Theme.defaultTextStyle
import com.dimmaranch.skull.state.GameAction
import com.dimmaranch.skull.state.GameState
import com.dimmaranch.skull.state.isUserRoomHost
import com.dimmaranch.skull.viewmodel.GameViewModel

class JoinRoomScreen(
    private val viewModel: GameViewModel,
    private val onJoinRoom: (String) -> Unit,
    private val onUpdateRoomCodeInput: (String) -> Unit,
) : Screen {

    @Composable
    override fun Content() {
        val roomCode = remember { mutableStateOf("") }
        var isJoinEnabled by remember { mutableStateOf(false) }

        val gameState: GameState by viewModel.gameState.collectAsState()
        val playerId by viewModel.userNameState.collectAsState()
        val navigator = LocalNavigator.current
        LaunchedEffect(gameState.canJoinRoom) {
            if (gameState.roomCode.isNotEmpty() && gameState.canJoinRoom) {
                navigator?.push(
                    CreateRoomScreen(
                        viewModel,
                        roomCode = gameState.roomCode,
                        roomHostId = gameState.hostId,
                        isHost = gameState.isUserRoomHost(playerId),
                        onStartGame = {
                            viewModel.handleAction(GameAction.StartGame)
                        }
                    )
                )
            }
        }

        // Enable the button when both fields are filled
        LaunchedEffect(roomCode.value) {
            isJoinEnabled = Utils.isRoomCodeValid(roomCode.value)
            onUpdateRoomCodeInput(roomCode.value)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Enter Your Friend's Room Code",
                style = defaultTextStyle
            )

            Spacer(modifier = Modifier.height(24.dp))

            CodeTextField(roomCode)

            Spacer(modifier = Modifier.height(16.dp))

            val noRoomMessage = gameState.noRoomMessage
            if (!noRoomMessage.isNullOrEmpty() && isJoinEnabled) {
                Text(
                    text = noRoomMessage,
                    style = defaultTextStyle.copy(color = Color.Red, fontSize = 16.sp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    onJoinRoom(roomCode.value)
                },
                enabled = isJoinEnabled
            ) {
                Text(text = "Join Room")
            }
        }
    }

    @Composable
    fun CodeTextField(roomCode: MutableState<String>) {
        OutlinedTextField(
            value = roomCode.value,
            onValueChange = { newValue ->
                // Allow only alphanumeric input and limit to 4 characters
                if (newValue.length <= 4 && newValue.all { it.isLetterOrDigit() }) {
                    roomCode.value = newValue.uppercase()
                }
            },
            label = { Text("Room Code") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Theme.SlateGray,
                unfocusedBorderColor = Color.Gray,
                backgroundColor = Theme.MidnightBlue,
                focusedLabelColor = Theme.SlateGray,
                unfocusedLabelColor = Theme.SlateGray,
            ),
            textStyle = defaultTextStyle.copy(fontSize = 16.sp, textAlign = TextAlign.Start),
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                capitalization = KeyboardCapitalization.Words
            )
        )
    }
}