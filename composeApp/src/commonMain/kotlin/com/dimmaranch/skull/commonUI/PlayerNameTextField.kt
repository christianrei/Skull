package com.dimmaranch.skull.commonUI

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dimmaranch.skull.viewmodel.GameViewModel
import com.dimmaranch.skull.commonUI.Theme.defaultTextStyle
import com.dimmaranch.skull.Utils.NAME_MAX_LENGTH

@Composable
fun PlayerNameTextField(gameVM: GameViewModel) {
    val playerId by gameVM.userNameState.collectAsState()

    OutlinedTextField(
        value = playerId,
        onValueChange = { newValue ->
            if (newValue.length <= NAME_MAX_LENGTH && newValue.all { it.isLetterOrDigit() }) {
                gameVM.setPlayerName(newValue)
            }
        },
        label = { Text("Your Name") },
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
        textStyle = defaultTextStyle.copy(fontSize = 16.sp),
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(
            capitalization = KeyboardCapitalization.Words
        )
    )
}