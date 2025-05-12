package com.dimmaranch.skull.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.dimmaranch.skull.commonUI.Theme.defaultTextStyle
import com.dimmaranch.skull.state.GameAction
import com.dimmaranch.skull.viewmodel.GameViewModel
import org.jetbrains.compose.resources.painterResource
import skull.composeapp.generated.resources.Res
import skull.composeapp.generated.resources.greenskull
import skull.composeapp.generated.resources.redskull

class HomeScreen(
    private val gameVM: GameViewModel
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(Res.drawable.redskull),
                contentDescription = null,
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Skull",
                style = defaultTextStyle.copy(fontSize = 40.sp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    navigator.push(
                        RoomOptionsScreen(
                            gameVM = gameVM,
                            navigateToJoin = {
                                navigator.push(
                                    JoinRoomScreen(
                                        viewModel = gameVM,
                                        onJoinRoom = { roomCode ->
                                            gameVM.joinGameRoom(gameCode = roomCode)
                                        },
                                        onUpdateRoomCodeInput = { gameVM.updateRoomCodeInput() },
                                    )
                                )
                            },
                            navigateToCreate = { playerName ->
                                gameVM.createGameRoom(hostPlayerId = playerName)
                                navigator.push(
                                    CreateRoomScreen(
                                        onStartGame = {
                                            gameVM.handleAction(GameAction.StartGame)
                                        },
                                        playerName = playerName,
                                        viewModel = gameVM
                                    )
                                )
                            })
                    )
                },
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) {
                Text("Play")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    navigator.push(
                        SettingsScreen(
                            onBackClicked = { navigator.pop() },
                            onNotificationsChanged = {}
                        ))
                },
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) {
                Text("Settings")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    navigator.push(RulesScreen({ navigator.pop() }))
                },
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) {
                Text("Rules")
            }
        }
    }
}
