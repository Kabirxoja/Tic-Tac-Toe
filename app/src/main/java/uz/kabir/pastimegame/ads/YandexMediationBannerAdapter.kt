package uz.kabir.pastimegame.ads

import android.content.Context
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
import org.json.JSONObject

class YandexMediationBannerAdapter : Adapter() {

    private var bannerView: BannerAdView? = null

    override fun getSDKVersionInfo(): VersionInfo {
        return VersionInfo(8, 1, 0)
    }

    override fun getVersionInfo(): VersionInfo {
        return VersionInfo(1, 0, 0)
    }

    override fun initialize(
        context: Context,
        initializationCompleteCallback: InitializationCompleteCallback,
        mediationConfigurations: List<MediationConfiguration?>
    ) {
        initializationCompleteCallback.onInitializationSucceeded()
    }

    override fun loadBannerAd(
        adConfiguration: MediationBannerAdConfiguration,
        adLoadCallback: MediationAdLoadCallback<MediationBannerAd?, MediationBannerAdCallback?>
    ) {
        val context = adConfiguration.context


        val yandexAdUnitId = try {
            JSONObject(adConfiguration.serverParameters.getString("parameter") ?: "").optString("AdUnitId")
        } catch (e: Exception) {
            adConfiguration.serverParameters.getString("parameter") ?: ""
        }

        bannerView = BannerAdView(context).apply {

            setAdSize(
                BannerAdSize.inline(
                    context,
                    adConfiguration.adSize.width,
                    adConfiguration.adSize.height
                )
            )

            setBannerAdEventListener(
                object : BannerAdEventListener {

                    override fun onAdLoaded() {
                        val mediationBannerAd = MediationBannerAd { this@apply }
                        adLoadCallback.onSuccess(mediationBannerAd)
                    }

                    override fun onAdFailedToLoad(error: AdRequestError) {

                        adLoadCallback.onFailure(
                            AdError(
                                error.code,
                                error.description,
                                "Yandex"
                            )
                        )
                    }

                    override fun onAdClicked() {}

                    override fun onImpression(
                        impressionData: ImpressionData?
                    ) {}
                }
            )

            loadAd(AdRequest.Builder(yandexAdUnitId).build())
        }
    }
}