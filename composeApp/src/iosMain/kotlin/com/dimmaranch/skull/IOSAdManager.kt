package com.dimmaranch.skull

//import cocoapods.GoogleMobileAds.*
import platform.Foundation.NSError
import platform.UIKit.UIViewController

class IosAdManager(private val viewController: UIViewController) : AdManager {

//    private var interstitial: GADInterstitialAd? = null

    override fun loadBannerAd() {
//        val adView = GADBannerView(adSize = kGADAdSizeBanner)
//        adView.adUnitID = "ca-app-pub-xxxxxxxxxxxxxxxx/banner" // Replace with actual ID
//        adView.rootViewController = viewController
//
//        val request = GADRequest.request()
//        adView.loadRequest(request)
//
//        // NOTE: You need to add `adView` to your view hierarchy manually.
//        viewController.view.addSubview(adView)
    }

    override fun loadInterstitialAd() {
//        val request = GADRequest.request()
//        GADInterstitialAd.loadWithAdUnitID(
//            "ca-app-pub-xxxxxxxxxxxxxxxx/interstitial", // Replace with actual ID
//            request,
//            object : GADInterstitialAdLoadCompletionHandler {
//                override fun invoke(ad: GADInterstitialAd?, error: NSError?) {
//                    if (error != null) {
//                        println("Failed to load interstitial: ${error.localizedDescription}")
//                        return
//                    }
//                    interstitial = ad
//                }
//            }
//        )
    }

    override fun showInterstitialAd() {
//        interstitial?.presentFromRootViewController(viewController)
    }
}
