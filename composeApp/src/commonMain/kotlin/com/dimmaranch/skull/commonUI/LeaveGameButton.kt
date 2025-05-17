package com.dimmaranch.skull.commonUI

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import com.dimmaranch.skull.viewmodel.GameViewModel

@Composable
fun LeaveGameButton(gameVM: GameViewModel, navigator: Navigator?) {
    var showLeaveDialog by remember { mutableStateOf(false) }

    // Top-right "X" button
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
        ) {
            IconButton(onClick = { showLeaveDialog = true }) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Leave Game",
                    tint = Color.Red
                )
            }
        }

        // Leave confirmation dialog
        if (showLeaveDialog) {
            AlertDialog(
                onDismissRequest = { showLeaveDialog = false },
                title = { Text("Leave Game?") },
                text = { Text("Are you sure you want to leave the current game?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showLeaveDialog = false
                            gameVM.clearGame()
                            navigator?.pop()
                        }
                    ) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLeaveDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}