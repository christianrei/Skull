package com.dimmaranch.skulls.commonUI

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dimmaranch.skulls.commonUI.Theme.defaultTextStyle
import com.dimmaranch.skulls.state.Card

//TODO Test out both options to see if stepper or slider is better
@Composable
fun BidStepper(
    currentBid: Int,
    cardsList: Map<String, List<Card>>,
    isCurrentTurn: Boolean,
    onBidChange: (Int) -> Unit
) {
    var bid by remember { mutableStateOf(currentBid + 1) }
    val maxBid = cardsList.values.flatten().size

    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = { if (bid > currentBid + 1) bid-- }) {
            //Icon remove?
            Icon(
                Icons.Default.ArrowBack,
                tint = Theme.SlateGray,
                contentDescription = "Decrease bid"
            )
        }

        Text(
            text = bid.toString(),
            fontSize = 20.sp,
            style = defaultTextStyle,
            modifier = Modifier.padding(8.dp)
        )

        IconButton(onClick = { if (bid < maxBid) bid++ }) {
            Icon(
                Icons.Default.Add,
                tint = Theme.SlateGray,
                contentDescription = "Increase bid"
            )
        }

        Button(onClick = { onBidChange(bid) }, enabled = (bid in (currentBid + 1)..maxBid) && isCurrentTurn) {
            Text("Bid")
        }
    }
}

@Composable
fun BidSlider(
    currentBid: Int,
    maxBid: Int,
    onBidChange: (Int) -> Unit
) {
    var bid by remember { mutableStateOf(currentBid + 1) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Bid: $bid", fontSize = 20.sp)

        Slider(
            value = bid.toFloat(),
            onValueChange = { bid = it.toInt() },
            valueRange = (currentBid + 1).toFloat()..maxBid.toFloat(),
            steps = maxBid - (currentBid + 1) - 1
        )

        Button(onClick = { onBidChange(bid) }) {
            Text("Confirm Bid")
        }
    }
}
