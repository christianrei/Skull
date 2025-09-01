package com.dimmaranch.skull

import android.app.Activity
import android.content.Context
import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class AndroidAdManager(private val context: Context) : AdManager {
    private var interstitialAd: InterstitialAd? = null

    @Composable
    override fun BannerAd() {
        AndroidView(factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                adUnitId = "ca-app-pub-7734392100739377/8285858475" // Replace with your Ad Unit ID
                loadAd(AdRequest.Builder().build())
            }
        })
    }


    override fun loadBannerAd() {
        val adView = AdView(context).apply {
            setAdSize(AdSize.BANNER)
            adUnitId = "ca-app-pub-7734392100739377/8285858475"
        }
        val adContainer = (context as Activity).findViewById<FrameLayout>(androidx.core.R.id.text)//.adContainer)
        adContainer.addView(adView)
        adView.loadAd(AdRequest.Builder().build())
    }

    override fun loadInterstitialAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(context, "ca-app-pub-7734392100739377/5931173783", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(ad: InterstitialAd) {
                interstitialAd = ad
            }
        })
    }

    override fun showInterstitialAd() {
        interstitialAd?.show(context as Activity)
    }
}
