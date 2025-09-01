@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.dimmaranch.skull

import androidx.compose.runtime.Composable
import googlemobileads.*
import googlemobileads.GADAdSize
import platform.Foundation.NSError
import platform.UIKit.UIViewController
import platform.CoreGraphics.CGSizeMake
import kotlinx.cinterop.cValue
import kotlinx.cinterop.CValue
import platform.UIKit.UIApplication


class IosAdManager() : AdManager {

    // Store UIViewController internally
    private val viewController: UIViewController by lazy {
        // Grab the current key window's rootViewController
        UIApplication.sharedApplication.keyWindow?.rootViewController
            ?: UIViewController()
    }

    private var interstitial: GADInterstitialAd? = null
    
    val kGADAdSizeBanner: CValue<GADAdSize> = cValue {
//        size = CGVectorMake(320.0, 50.0) // or CGSizeMake if size is CGSize

        //this.width = 320.0
        //this.height = 50.0
    }

    @Composable
    override fun BannerAd() {
        val vc = viewController
        val adView = GADBannerView(frame = platform.CoreGraphics.CGRectMake(0.0, 0.0, 320.0, 50.0))
        adView.adUnitID = "ca-app-pub-7734392100739377/7272382442"
        adView.rootViewController = vc
        adView.loadRequest(GADRequest.request())
        vc.view.addSubview(adView)
    }

    override fun loadBannerAd() {
        val adView = GADBannerView(adSize = kGADAdSizeBanner)

        adView.adUnitID = "ca-app-pub-7734392100739377/7272382442" // Replace with actual ID
        adView.rootViewController = viewController

        val request = GADRequest.request()
        adView.loadRequest(request)

        // NOTE: You need to add `adView` to your view hierarchy manually.
        viewController.view.addSubview(adView)
    }

    override fun loadInterstitialAd() {
        val request = GADRequest.request()
        GADInterstitialAd.loadWithAdUnitID(
            "ca-app-pub-7734392100739377/3647449312", // Replace with actual ID
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
