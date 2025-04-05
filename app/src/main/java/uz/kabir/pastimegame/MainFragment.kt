package uz.kabir.pastimegame

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
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

        binding.btnTwoPlayer.setOnClickListener {
            val bottomSheetFragment = BottomSheetTwoPlayer("pair")
            bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
        }

        binding.btnOnePlayer.setOnClickListener {
            val bottomSheetOnePlayer = BottomSheetOnePlayer()
            bottomSheetOnePlayer.show(childFragmentManager, bottomSheetOnePlayer.tag)
        }
        binding.btnPuzzle.setOnClickListener {
            val bottomSheetFragment = BottomSheetTwoPlayer("bolt")
            bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
        }




        return root
    }
}
