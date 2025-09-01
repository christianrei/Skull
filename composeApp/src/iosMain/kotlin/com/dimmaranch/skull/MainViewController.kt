package com.dimmaranch.skull

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import com.dimmaranch.skull.commonUI.Theme.MidnightBlue
import com.dimmaranch.skull.viewmodel.GameViewModel
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    return ComposeUIViewController {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .background(MidnightBlue)
        ) {
            VoyagerAppNavigation(
                GameViewModel(),
                IosAdManager()
            )
        }
    }
}