package com.dimmaranch.skulls.screen

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dimmaranch.skulls.commonUI.Theme.defaultTextStyle

@Composable
fun RulesScreen(onBackClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize().scrollable(rememberScrollState(), orientation = Orientation.Vertical),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Back Button
        IconButton(
            onClick = { onBackClick() },
            modifier = Modifier.padding(8.dp)
        ) {
            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
        }

        // Title
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Skulls Game Rules",
                style = defaultTextStyle,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Rule sections (same as before)
            RuleSection(
                title = "Objective",
                content = "The goal is to win bids and reveal cards without being challenged by other players."
            )

            RuleSection(
                title = "Setup",
                content = "Each player receives a set of cards: 3 Skulls and 1 Rose."
            )

            RuleSection(
                title = "Placing Phase",
                content = "Players take turns placing cards face-down, without revealing them to others."
            )

            RuleSection(
                title = "Bidding Phase",
                content = "Players place bids to reveal how many cards they think they can safely reveal without showing a Skull."
            )

            RuleSection(
                title = "Revealing Phase",
                content = "The player with the highest bid reveals their cards. If a Skull is revealed, they lose the round."
            )

            RuleSection(
                title = "Elimination",
                content = "When you lose a round, a card is taken away from your hand. If you run out of cards, you are eliminated from the game."
            )

            RuleSection(
                title = "Winning",
                content = "When a player successfully reveals cards with revealing a Skull they get a point. Earning 2 points or being the last one standing wins the game."
            )
        }
    }
}

@Composable
fun RuleSection(title: String, content: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = title,
            style = defaultTextStyle.copy(fontSize = 16.sp),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = content,
            style = defaultTextStyle.copy(fontSize = 16.sp),
            modifier = Modifier.padding(bottom = 16.dp)
        )
    }
}
