package uz.kabir.pastimegame.screens

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import uz.kabir.pastimegame.AnimationButton.animateClick
import uz.kabir.pastimegame.screens.BottomSheetOnePlayer
import uz.kabir.pastimegame.screens.BottomSheetTwoPlayer
import uz.kabir.pastimegame.MySharedPreference
import uz.kabir.pastimegame.R
import uz.kabir.pastimegame.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        val root = binding.root



        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnTwoPlayer.setOnClickListener {
            val bottomSheetTwoPlayer = BottomSheetTwoPlayer("pair")
            bottomSheetTwoPlayer.show(childFragmentManager, bottomSheetTwoPlayer.tag)
            binding.btnTwoPlayer.animateClick()
        }

        binding.btnOnePlayer.setOnClickListener {
            val bottomSheetOnePlayer = BottomSheetOnePlayer()
            bottomSheetOnePlayer.show(childFragmentManager, bottomSheetOnePlayer.tag)
            binding.btnOnePlayer.animateClick()
        }
        binding.btnInfinite.setOnClickListener {
            val bottomSheetTwoPlayer = BottomSheetTwoPlayer("bolt")
            bottomSheetTwoPlayer.show(childFragmentManager, bottomSheetTwoPlayer.tag)
            binding.btnInfinite.animateClick()
        }

        binding.btnLanguage.setOnClickListener {
            val bottomSheetLanguage = BottomSheetLanguage()
            bottomSheetLanguage.show(childFragmentManager, bottomSheetLanguage.tag)
            binding.btnLanguage.animateClick()
        }

        if (MySharedPreference.getStateAudio(requireContext())) {
            binding.btnAudio.setImageResource(R.drawable.ic_audio)
        } else {
            binding.btnAudio.setImageResource(R.drawable.ic_audio_no)
        }

        binding.btnAudio.setOnClickListener {
            if (MySharedPreference.getStateAudio(requireContext())) {
                binding.btnAudio.setImageResource(R.drawable.ic_audio_no)
                MySharedPreference.saveStateAudio(requireContext(), false)
            } else {
                binding.btnAudio.setImageResource(R.drawable.ic_audio)
                MySharedPreference.saveStateAudio(requireContext(), true)
            }
            binding.btnAudio.animateClick()
            Toast.makeText(
                binding.root.context,
                "Audio: ${
                    if (MySharedPreference.getStateAudio(requireContext())) getString(R.string.audio_on) else getString(
                        R.string.audio_off
                    )
                }",
                Toast.LENGTH_SHORT
            ).show()
        }



        binding.adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                Log.d("Mediation", "✅ AdMob banner yuklandi")
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d("Mediation", "❌ AdMob banner yuklanmadi: ${adError.message}")
            }

            override fun onAdOpened() {
                Log.d("Mediation", "ℹ️ AdMob ochildi")
            }

            override fun onAdClicked() {
                Log.d("Mediation", "👆 AdMob bosildi")
            }

            override fun onAdClosed() {
                Log.d("Mediation", "↩️ Ad yopildi")
            }
        }

        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)
        Log.d("Mediation", "AdMob yuklandi")


    }


    override fun onPause() {
        binding.adView.pause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        binding.adView.resume()
    }

    override fun onDestroyView() {
        binding.adView.destroy()
        super.onDestroyView()
    }


}