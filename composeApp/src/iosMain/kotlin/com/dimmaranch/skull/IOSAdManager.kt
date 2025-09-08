@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.dimmaranch.skull

import androidx.compose.runtime.Composable
import googlemobileads.*
import platform.Foundation.NSError
import platform.UIKit.UIViewController
import platform.CoreGraphics.CGRectGetWidth
import platform.UIKit.NSLayoutConstraint
import platform.UIKit.UIApplication


class IosAdManager : AdManager {

    private val viewController: UIViewController by lazy {
        // Grab the current key window's rootViewController
        UIApplication.sharedApplication.keyWindow?.rootViewController
            ?: UIViewController()
    }

    private var interstitial: GADInterstitialAd? = null
    
    @Composable
    override fun BannerAd() {
        val vc = viewController
        val width = CGRectGetWidth(vc.view.bounds)
        val size = GADCurrentOrientationAnchoredAdaptiveBannerAdSizeWithWidth(width)

        val adView = GADBannerView(adSize = size)
        adView.adUnitID = "ca-app-pub-7734392100739377/7272382442"
        adView.rootViewController = vc

        vc.view.addSubview(adView)
        adView.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint.activateConstraints(
            listOf(
                adView.bottomAnchor.constraintEqualToAnchor(vc.view.safeAreaLayoutGuide.bottomAnchor),
                adView.centerXAnchor.constraintEqualToAnchor(vc.view.centerXAnchor)
            )
        )

        adView.loadRequest(GADRequest.request())
    }

    override fun loadBannerAd() {
        val vc = viewController
        val width = CGRectGetWidth(vc.view.bounds)
        val size = GADCurrentOrientationAnchoredAdaptiveBannerAdSizeWithWidth(width)

        val adView = GADBannerView(adSize = size)
        adView.adUnitID = "ca-app-pub-7734392100739377/7272382442"
        adView.rootViewController = vc

        vc.view.addSubview(adView)
        adView.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint.activateConstraints(
            listOf(
                adView.bottomAnchor.constraintEqualToAnchor(vc.view.safeAreaLayoutGuide.bottomAnchor),
                adView.centerXAnchor.constraintEqualToAnchor(vc.view.centerXAnchor)
            )
        )

        adView.loadRequest(GADRequest.request())
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
