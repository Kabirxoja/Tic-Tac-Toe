package uz.kabir.pastimegame

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import uz.kabir.pastimegame.AnimationButton.animateClick
import uz.kabir.pastimegame.databinding.FragmentMainBinding
import java.util.Locale


class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        val root = binding.root

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






        return root
    }





}
