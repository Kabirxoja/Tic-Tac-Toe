package uz.kabir.pastimegame

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import uz.kabir.pastimegame.databinding.FragmentBottomSheetLanguageBinding
import uz.kabir.pastimegame.databinding.RecycleItemLayoutBinding
import java.util.Locale

private const val PREF_LANGUAGE_KEY = "app_language"

class BottomSheetLanguage : BottomSheetDialogFragment() {

    private var _binding: FragmentBottomSheetLanguageBinding? = null
    private val binding get() = _binding!!
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
    private lateinit var languageAdapter: LanguageAdapter

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

        languageAdapter = LanguageAdapter(languageDataList)
        binding.recyclerView.adapter = languageAdapter
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)

        languageAdapter.setOnClickListener {
            selectedLanguageCode = it.iconCode
            selectedLanguageCode?.let { code ->
                saveLanguage(requireContext(), code)
                updateAppLocale(requireContext(), code)
                Toast.makeText(binding.root.context, "Language Updated", Toast.LENGTH_SHORT).show()
                requireActivity().recreate() // Recreate the activity to apply the new locale
                dismiss()
            } ?: run {
                Toast.makeText(requireContext(), "Please select a language", Toast.LENGTH_SHORT).show()
            }
        }


    }

    private fun saveLanguage(context: Context, languageCode: String) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        sharedPreferences.edit().putString(PREF_LANGUAGE_KEY, languageCode).apply()
    }

    private fun updateAppLocale(context: Context, languageCode: String): Context {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val resources = context.resources
        val config = Configuration(resources.configuration)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}