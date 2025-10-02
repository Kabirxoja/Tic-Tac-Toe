package uz.kabir.pastimegame.ads

import android.content.Context
import android.view.View
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.VersionInfo
import com.google.android.gms.ads.mediation.Adapter
import com.google.android.gms.ads.mediation.InitializationCompleteCallback
import com.google.android.gms.ads.mediation.MediationAdLoadCallback
import com.google.android.gms.ads.mediation.MediationBannerAd
import com.google.android.gms.ads.mediation.MediationBannerAdCallback
import com.google.android.gms.ads.mediation.MediationBannerAdConfiguration
import com.google.android.gms.ads.mediation.MediationConfiguration
import com.yandex.mobile.ads.banner.BannerAdEventListener
import com.yandex.mobile.ads.banner.BannerAdSize
import com.yandex.mobile.ads.banner.BannerAdView
import com.yandex.mobile.ads.common.AdRequest
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import com.yandex.mobile.ads.common.MobileAds

class YandexMediationBannerAdapter : Adapter() {

    private var bannerView: BannerAdView? = null


    override fun getSDKVersionInfo(): VersionInfo {
        return VersionInfo(7, 15, 2)
    }

    override fun getVersionInfo(): VersionInfo {
        return VersionInfo(1, 0, 0)
    }

    override fun initialize(
        context: Context,
        initializationCompleteCallback: InitializationCompleteCallback,
        mediationConfigurations: List<MediationConfiguration?>
    ) {
        MobileAds.initialize(context) {
            initializationCompleteCallback.onInitializationSucceeded()
        }
    }

    override fun loadBannerAd(
        adConfiguration: MediationBannerAdConfiguration,
        adLoadCallback: MediationAdLoadCallback<MediationBannerAd?, MediationBannerAdCallback?>
    ) {
        super.loadBannerAd(adConfiguration, adLoadCallback)

        val context = adConfiguration.context
        val yandexAdUnitId = adConfiguration.serverParameters.getString("parameter") ?: ""

        bannerView = BannerAdView(context).apply {
            setAdUnitId(yandexAdUnitId)
            setAdSize(
                BannerAdSize.inlineSize(
                    context,
                    adConfiguration.adSize.width,
                    adConfiguration.adSize.height
                )
            )

            setBannerAdEventListener(object : BannerAdEventListener {
                override fun onAdLoaded() {
                    val ad = object : MediationBannerAd {
                        override fun getView(): View = this@apply
                    }
                    val callback = adLoadCallback.onSuccess(ad)
                    callback.onAdOpened() // optional
                }

                override fun onAdFailedToLoad(error: AdRequestError) {
                    adLoadCallback.onFailure(
                        AdError(error.code, error.description, "Yandex")
                    )
                }

                override fun onAdClicked() {
                    // Banner bosilganda callback
                }

                override fun onReturnedToApplication() {}
                override fun onLeftApplication() {}
                override fun onImpression(impressionData: ImpressionData?) {}
            })

            loadAd(AdRequest.Builder().build())

        }
    }
}