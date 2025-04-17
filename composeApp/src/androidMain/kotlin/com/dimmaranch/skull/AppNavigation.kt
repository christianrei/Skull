package com.dimmaranch.skull

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dimmaranch.skull.screen.CreateRoomScreen
import com.dimmaranch.skull.state.GameAction
import com.dimmaranch.skull.screen.GameScreen
import com.dimmaranch.skull.state.GameState
import com.dimmaranch.skull.viewmodel.GameViewModel
import com.dimmaranch.skull.screen.HomeScreen
import com.dimmaranch.skull.screen.JoinRoomScreen
import com.dimmaranch.skull.state.Phase
import com.dimmaranch.skull.screen.RoomOptionsScreen
import com.dimmaranch.skull.screen.RulesScreen
import com.dimmaranch.skull.screen.SettingsScreen
import com.dimmaranch.skull.state.isUserRoomHost

object NavRoute {
    const val HOME = "home"
    const val PLAY = "play"
    const val CREATE = "create"
    const val JOIN = "join"
    const val START = "start"
    const val SETTINGS = "settings"
    const val RULES = "rules"
}

@Composable
fun AppNavigation(gameVM: GameViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = NavRoute.HOME) {
        composable(NavRoute.HOME) {
            gameVM.signInAnon()
            HomeScreen(
                onPlayClick = { navController.navigate(NavRoute.PLAY) },
                onSettingsClick = { navController.navigate(NavRoute.SETTINGS) },
                onRulesClick = { navController.navigate(NavRoute.RULES) }
            )
        }
        composable(NavRoute.PLAY) {
            RoomOptionsScreen(
                gameVM = gameVM,
                navigateToCreate = { playerName ->
                    gameVM.createGameRoom(hostPlayerId = playerName)
                    navController.navigate(NavRoute.CREATE)
                },
                navigateToJoin = {
                    navController.navigate(NavRoute.JOIN)
                }
            )
        }
        composable(NavRoute.CREATE) {
            val gameState: GameState by gameVM.gameState.collectAsState()
            val playerId by gameVM.userNameState.collectAsState()
            LaunchedEffect(gameState.phase) {
                if (gameState.phase == Phase.PLACING_FIRST_CARD) {
                    navController.navigate(NavRoute.START)
                }
            }
            CreateRoomScreen(
                gameVM,
                roomCode = gameState.roomCode,
                roomHostId = gameState.hostId,
                isHost = gameState.isUserRoomHost(playerId),
                players = gameState.players.values.toList(),
                onStartGame = {
                    gameVM.handleAction(GameAction.StartGame)
                }
            )
        }
        composable(NavRoute.JOIN) {
            val gameState: GameState by gameVM.gameState.collectAsState()
            LaunchedEffect(gameState.canJoinRoom) {
                if (gameState.roomCode.isNotEmpty() && gameState.canJoinRoom) {
                    navController.navigate(NavRoute.CREATE)
                }
            }
            JoinRoomScreen(
                onJoinRoom = { roomCode ->
                    gameVM.joinGameRoom(gameCode = roomCode)
                },
                onUpdateRoomCodeInput = { gameVM.updateRoomCodeInput() },
                noRoomMessage = gameState.noRoomMessage
            )
        }
        composable(NavRoute.START) {
            GameScreen(
                viewModel = gameVM,
                onEndGame = { navController.navigate(NavRoute.HOME) }
            )
        }
        composable(NavRoute.SETTINGS) {
            SettingsScreen(
                onBackClicked = { navController.popBackStack() },
                onSoundChanged = {},
                onNotificationsChanged = {},
            )
        }
        composable(NavRoute.RULES) {
            RulesScreen {
                navController.popBackStack()
            }
        }
    }
}
