package uz.kabir.pastimegame.screens

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import uz.kabir.pastimegame.LanguageAdapter
import uz.kabir.pastimegame.LanguageItem
import uz.kabir.pastimegame.databinding.FragmentBottomSheetLanguageBinding
import java.util.Locale

class BottomSheetLanguage : BottomSheetDialogFragment() {

    companion object {
        private const val PREF_LANGUAGE_KEY = "app_language"
    }

    private var _binding: FragmentBottomSheetLanguageBinding? = null
    private val binding get() = _binding!!
    private lateinit var languageAdapter: LanguageAdapter

    private lateinit var languageRecyclerView: RecyclerView
    private var selectedLanguageCode: String? = null
    private val languageDataList = listOf(
        LanguageItem("uz", "Uzbek"),
        LanguageItem("kaa", "Karakalpak"),
        LanguageItem("en", "English"),
        LanguageItem("ru", "Russian"),
        LanguageItem("es", "Spanish"),
        LanguageItem("fr", "French")
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBottomSheetLanguageBinding.inflate(inflater, container, false)
        languageRecyclerView = binding.recyclerView
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val savedSelectedLanguageCode = getSavedSelectedLanguageCode(requireContext())

        languageAdapter =
            LanguageAdapter(languageDataList, savedSelectedLanguageCode) // Pass the saved code
        binding.recyclerView.adapter = languageAdapter
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)

        languageAdapter.setOnClickListener { item ->
            selectedLanguageCode = item.iconCode
            selectedLanguageCode?.let { code ->
                saveLanguage(requireContext(), code) // Save the icon code as well
                updateAppLocale(requireContext(), code)
                requireActivity().recreate()
                dismiss()
            } ?: run {
                Toast.makeText(requireContext(), "Please select a language", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun saveLanguage(context: Context, languageCode: String) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        sharedPreferences.edit().putString(PREF_LANGUAGE_KEY, languageCode).apply()
    }


    private fun getSavedSelectedLanguageCode(context: Context): String? {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.getString(PREF_LANGUAGE_KEY, null)
    }

    private fun updateAppLocale(context: Context, languageCode: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ (API 33+)
            val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(languageCode)
            AppCompatDelegate.setApplicationLocales(appLocale)
        } else {
            // Android 7.0 - Android 12 (API 24-32)
            val locale = Locale(languageCode)
            Locale.setDefault(locale)
            val resources = context.resources
            val config = Configuration(resources.configuration)
            config.setLocale(locale)
            config.setLayoutDirection(locale)
            context.createConfigurationContext(config)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}