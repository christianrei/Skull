package com.dimmaranch.skulls.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dimmaranch.skulls.commonUI.Theme.defaultTextStyle

@Composable
fun HomeScreen(
    onPlayClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onRulesClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Skulls Game",
            style = defaultTextStyle.copy(fontSize = 40.sp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onPlayClick,
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Text("Play")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onSettingsClick,
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Text("Settings")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onRulesClick,
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Text("Rules")
        }
    }
}
