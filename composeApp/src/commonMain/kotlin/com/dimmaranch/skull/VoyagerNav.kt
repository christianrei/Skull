package com.dimmaranch.skull

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.dimmaranch.skull.screen.CreateRoomScreen
import com.dimmaranch.skull.screen.GameScreen
import com.dimmaranch.skull.screen.HomeScreen
import com.dimmaranch.skull.screen.JoinRoomScreen
import com.dimmaranch.skull.screen.RoomOptionsScreen
import com.dimmaranch.skull.screen.RulesScreen
import com.dimmaranch.skull.screen.SettingsScreen
import com.dimmaranch.skull.state.GameAction
import com.dimmaranch.skull.state.GameState
import com.dimmaranch.skull.state.Phase
import com.dimmaranch.skull.state.isUserRoomHost
import com.dimmaranch.skull.viewmodel.GameViewModel

@Composable
fun VoyagerAppNavigation(
    gameVM: GameViewModel,
    activity: PlatformActivity
) {
    gameVM.signInAnon()
    if (gameVM.shouldRejoinGame()) {
        val navigator = LocalNavigator.current
        Navigator(GameScreen(gameVM, onEndGame = { navigator?.push(HomeScreen(gameVM, activity)) }))
    } else {
        Navigator(HomeScreen(gameVM, activity))
    }
    //TODO Add bottom section ads for all screens except GameScreen
}
