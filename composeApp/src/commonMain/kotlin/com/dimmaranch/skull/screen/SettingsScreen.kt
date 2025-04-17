package com.dimmaranch.skulls.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
    onBackClicked: () -> Unit, // To navigate back to the previous screen
    onSoundChanged: (Boolean) -> Unit, // Sound toggle
    onNotificationsChanged: (Boolean) -> Unit // Notifications toggle
) {
    var skullImageUri = remember { mutableStateOf<ByteArray?>(null) }
    var roseImageUri = remember { mutableStateOf<ByteArray?>(null) }

    // This is where you would typically manage your settings
    val soundEnabled = remember { true } // Default sound enabled
    val notificationsEnabled = remember { true } // Default notifications enabled

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Settings",
//            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Sound setting
        SettingsSwitch(
            label = "Sound",
            checked = soundEnabled,
            onCheckedChange = onSoundChanged
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Notifications setting
        SettingsSwitch(
            label = "Notifications",
            checked = notificationsEnabled,
            onCheckedChange = onNotificationsChanged
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Back button to navigate to previous screen
        Button(
            onClick = onBackClicked,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Back")
        }
    }
}

@Composable
fun SettingsSwitch(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
//            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}
