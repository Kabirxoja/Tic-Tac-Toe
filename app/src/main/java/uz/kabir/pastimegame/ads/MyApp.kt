package uz.kabir.pastimegame.ads

import android.app.Application
import com.google.android.gms.ads.MobileAds

class MyApp: Application() {
    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this)
    }
}