package com.dimmaranch.skull

import androidx.compose.runtime.Composable

interface AdManager {
    @Composable
    fun BannerAd() // Composable for banner
    fun loadBannerAd()
    fun loadInterstitialAd()
    fun showInterstitialAd()
}