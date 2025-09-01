@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.dimmaranch.skull

import googlemobileads.*
import googlemobileads.GADAdSize
import platform.Foundation.NSError
import platform.UIKit.UIViewController
import platform.CoreGraphics.CGSizeMake
import kotlinx.cinterop.cValue
import kotlinx.cinterop.CValue


class IosAdManager(private val viewController: UIViewController) : AdManager {

    private var interstitial: GADInterstitialAd? = null
    
    val kGADAdSizeBanner: CValue<GADAdSize> = cValue {
//        size = CGVectorMake(320.0, 50.0) // or CGSizeMake if size is CGSize

        //this.width = 320.0
        //this.height = 50.0
    }

    override fun loadBannerAd() {
        val adView = GADBannerView(adSize = kGADAdSizeBanner)

        adView.adUnitID = "ca-app-pub-xxxxxxxxxxxxxxxx/banner" // Replace with actual ID
        adView.rootViewController = viewController

        val request = GADRequest.request()
        adView.loadRequest(request)

        // NOTE: You need to add `adView` to your view hierarchy manually.
        viewController.view.addSubview(adView)
    }

    override fun loadInterstitialAd() {
        val request = GADRequest.request()
        GADInterstitialAd.loadWithAdUnitID(
            "ca-app-pub-xxxxxxxxxxxxxxxx/interstitial", // Replace with actual ID
            request,
            object : GADInterstitialAdLoadCompletionHandler {
                override fun invoke(ad: GADInterstitialAd?, error: NSError?) {
                    if (error != null) {
                        println("Failed to load interstitial: ${error.localizedDescription}")
                        return
                    }
                    interstitial = ad
                }
            }
        )
    }

    override fun showInterstitialAd() {
        interstitial?.presentFromRootViewController(viewController)
    }
}
