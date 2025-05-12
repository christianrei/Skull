package com.dimmaranch.skull.screen

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import com.dimmaranch.skull.commonUI.Theme.defaultTextStyle

class SettingsScreen(
    private val onBackClicked: () -> Unit,
    private val onNotificationsChanged: (Boolean) -> Unit
) : Screen {

    @Composable
    override fun Content() {
        var skullImageUri = remember { mutableStateOf<ByteArray?>(null) }
        var roseImageUri = remember { mutableStateOf<ByteArray?>(null) }

        // This is where you would typically manage your settings
        val soundEnabled = remember { true } // Default sound enabled
        val notificationsEnabled = remember { true } // Default notifications enabled

        Column(
            modifier = Modifier
                .fillMaxSize()
                .scrollable(rememberScrollState(), orientation = Orientation.Vertical),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Back Button
            IconButton(
                onClick = { onBackClicked() },
                modifier = Modifier.padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    tint = Color.White,
                    contentDescription = "Back"
                )
            }

            Text(
                text = "Settings",
                style = defaultTextStyle.copy(fontSize = 40.sp),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Notifications setting
            SettingsSwitch(
                label = "Notifications",
                checked = notificationsEnabled,
                onCheckedChange = onNotificationsChanged
            )

            Spacer(modifier = Modifier.height(32.dp))
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
}
