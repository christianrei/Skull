package com.dimmaranch.skulls.commonUI

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.dimmaranch.skull.getCustomFontFamily

object Theme {
    val MidnightBlue = Color(0xFF262051)
    val SlateGray = Color(0xFF736698)
    val PrimaryText = Color(0xFF909192)
    val SecondaryText = Color(0xFFDC6D45)
    val SkullGreen = Color(0xFF70BD7C)
    val SkullPink = Color(0XFFE28BBB)
    val PokerTableGreen = Color(0xFF228B22)

    val defaultTextStyle = TextStyle(
        fontSize = 24.sp,
        fontFamily = getCustomFontFamily(),
        fontWeight = FontWeight.Normal,
        color = PrimaryText
    )
}