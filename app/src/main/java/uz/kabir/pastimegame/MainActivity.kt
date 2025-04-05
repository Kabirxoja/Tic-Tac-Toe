package uz.kabir.pastimegame

import android.content.Context
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import uz.kabir.pastimegame.databinding.ActivityMainBinding
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private val PREF_LANGUAGE_KEY = "app_language"

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



    }


    override fun attachBaseContext(base: Context) {
        val savedLanguage = getSavedLanguage(base)
        val newBase = savedLanguage?.let { updateAppLocaleBase(base, it) } ?: base
        super.attachBaseContext(newBase)
    }


    private fun getSavedLanguage(context: Context): String? {
        val sharedPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.getString(PREF_LANGUAGE_KEY, null)
    }

    private fun updateAppLocaleBase(context: Context, languageCode: String): Context {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val resources = context.resources
        val config = android.content.res.Configuration(resources.configuration)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            config.setLocale(locale)
            config.setLayoutDirection(locale)
            return context.createConfigurationContext(config)
        } else {
            config.locale = locale
            config.setLayoutDirection(locale)
            resources.updateConfiguration(config, resources.displayMetrics)
            return context
        }
    }
}