package com.dimmaranch.skull

import com.dimmaranch.skulls.AdManager
import platform.UIKit.UIViewController
//import cocoapods.GoogleMobileAds.*

class IosAdManager(private val viewController: UIViewController) : AdManager {
//    private var interstitial: GADInterstitialAd? = null

    override fun loadBannerAd() {
//        val adView = GADBannerView()
//        adView.adUnitID = "ca-app-pub-xxxxxxxxxxxxxxxx/banner" // Replace with your Ad Unit ID
//        adView.rootViewController = viewController
//        adView.loadRequest(GADRequest())
    }

    override fun loadInterstitialAd() {
//        GADInterstitialAd.loadWithAdUnitID(
//            "ca-app-pub-xxxxxxxxxxxxxxxx/interstitial",
//            GADRequest(),
//            object : GADInterstitialAdLoadCompletionHandler {
//                override fun invoke(ad: GADInterstitialAd?, error: NSError?) {
//                    if (error == null) interstitial = ad
//                }
//            }
//        )
    }

    override fun showInterstitialAd() {
//        interstitial?.presentFromRootViewController(viewController)
    }
}
