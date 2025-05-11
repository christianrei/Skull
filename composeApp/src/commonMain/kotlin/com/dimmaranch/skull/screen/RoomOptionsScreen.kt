package com.dimmaranch.skull.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import com.dimmaranch.skull.Utils
import com.dimmaranch.skull.commonUI.PlayerNameTextField
import com.dimmaranch.skull.commonUI.Theme.defaultTextStyle
import com.dimmaranch.skull.viewmodel.GameViewModel

class RoomOptionsScreen(
    private val gameVM: GameViewModel,
    private val navigateToCreate: (String) -> Unit,
    private val navigateToJoin: (String) -> Unit
) : Screen {

    @Composable
    override fun Content() {
        val playerId by gameVM.userNameState.collectAsState()
        var areButtonsEnabled by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LaunchedEffect(playerId) {
                areButtonsEnabled = Utils.isPlayerNameValid(playerId)
            }

//        Image(
//            painter = painterResource("drawable/title_logo.xml"),
//            contentDescription = "SVG Image"
//        )

            Text(
                text = "Skulls Game",
                style = defaultTextStyle.copy(fontSize = 40.sp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Set your name then Create a room or Join a room by entering the host's code",
                textAlign = TextAlign.Center,
                style = defaultTextStyle
            )

            PlayerNameTextField(gameVM)

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                enabled = areButtonsEnabled,
                onClick = {
                    navigateToCreate.invoke(playerId)
                }
            ) {
                Text(text = "Create a Room")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                enabled = areButtonsEnabled,
                onClick = {
                    navigateToJoin.invoke(playerId)
                }
            ) {
                Text(text = "Join a Room")
            }
        }
    }
}
