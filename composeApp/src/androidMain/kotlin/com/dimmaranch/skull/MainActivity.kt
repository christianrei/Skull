package com.dimmaranch.skull

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.Modifier
import com.dimmaranch.skull.commonUI.Theme.MidnightBlue
import com.dimmaranch.skull.viewmodel.GameViewModel
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        val viewModel = GameViewModel()
        installSplashScreen()
        setContent {
            SkullsTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .background(MidnightBlue)
                ) {
                    VoyagerAppNavigation(viewModel)
//                    AppNavigation(viewModel)
                }
            }
        }
    }
}