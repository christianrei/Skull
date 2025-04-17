package com.dimmaranch.skull

import android.app.Activity
import android.content.Context
import android.widget.FrameLayout
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class AndroidAdManager(private val context: Context) : AdManager {
    private var interstitialAd: InterstitialAd? = null

    override fun loadBannerAd() {
        val adView = AdView(context).apply {
//            adSize = AdSize.BANNER
            adUnitId = "ca-app-pub-xxxxxxxxxxxxxxxx/banner" // Replace with your Ad Unit ID
        }
        val adContainer = (context as Activity).findViewById<FrameLayout>(androidx.core.R.id.text)//.adContainer)
        adContainer.addView(adView)
        adView.loadAd(AdRequest.Builder().build())
    }

    override fun loadInterstitialAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(context, "ca-app-pub-xxxxxxxxxxxxxxxx/interstitial", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(ad: InterstitialAd) {
                interstitialAd = ad
            }
        })
    }

    override fun showInterstitialAd() {
        interstitialAd?.show(context as Activity)
    }
}
