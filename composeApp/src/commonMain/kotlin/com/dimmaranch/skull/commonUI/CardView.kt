package com.dimmaranch.skull.commonUI

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dimmaranch.skull.Utils
import com.dimmaranch.skull.state.Card
import org.jetbrains.compose.resources.painterResource

@Composable
fun CardView(
    card: Card,
    playerIndex: Int,
    isSelectable: Boolean,
    isFaceUp: Boolean = false,
    isAnimating: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    var showConfirmation by remember { mutableStateOf(false) }
    val cardImage = if (isFaceUp) Utils.mapPlayerIndexToDrawable(
        playerIndex,
        card == Card.SKULL,
        card == Card.ROSE
    ) else Utils.mapPlayerIndexToDrawable(playerIndex)
    val clickableModifier = if (isSelectable) {
        PulsingBorder().clickable { onClick?.invoke() }
    } else {
        Modifier
    }

    val boxSize = if (isAnimating) 96.dp else if (isSelectable) 64.dp else 56.dp
    val imageSize = if (isAnimating) 88.dp else if (isSelectable) 56.dp else 48.dp
    Box(
        contentAlignment = Alignment.Center,
        modifier = clickableModifier
            .size(boxSize)
            .let { if (onClick != null) it.clickable { onClick() } else it }
            .clickable(enabled = isSelectable) { showConfirmation = true }
    ) {
        Image(
            painter = painterResource(cardImage),
            contentDescription = null,
            modifier = Modifier.size(imageSize)
        )
        if (showConfirmation) {
            ConfirmationDialog(
                onConfirm = {
                    showConfirmation = false
                    onClick?.invoke()
                },
                onDismiss = { showConfirmation = false }
            )
        }
    }
}