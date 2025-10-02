package uz.kabir.pastimegame.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import uz.kabir.pastimegame.AnimationButton.animateClick
import uz.kabir.pastimegame.R
import uz.kabir.pastimegame.databinding.FragmentBottomSheetBinding

class BottomSheetTwoPlayer(private var selected: String) : BottomSheetDialogFragment() {

    private var _binding: FragmentBottomSheetBinding? = null
    private val binding get() = _binding!!

    private var user1ImageIndex = 0
    private var user2ImageIndex = 1

    private val userImages = arrayOf(
        R.drawable.ic_character_woman_1,
        R.drawable.ic_character_woman_5,
        R.drawable.ic_character_woman_4,
        R.drawable.ic_character_man_3,
        R.drawable.ic_character_man_2,
        R.drawable.ic_character_man_4,
    )


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBottomSheetBinding.inflate(inflater, container, false)
        if (selected == "bolt") {
            binding.root.setBackgroundResource(R.drawable.ic_background_main_4)
            binding.editTextContainer.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.green_light)
            user2ImageIndex = 2
            user1ImageIndex = 3
        } else {
            binding.root.setBackgroundResource(R.drawable.ic_background_main_3)
            binding.editTextContainer.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.purple_light)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.userImageX.setBackgroundResource(userImages[user1ImageIndex])
        binding.userImageO.setBackgroundResource(userImages[user2ImageIndex])

        binding.userImageX.setOnClickListener {
            user1ImageIndex = (user1ImageIndex + 1) % userImages.size
            binding.userImageX.setBackgroundResource(userImages[user1ImageIndex])
            binding.userImageX.animateClick()
        }

        binding.userImageO.setOnClickListener {
            user2ImageIndex = (user2ImageIndex + 1) % userImages.size
            binding.userImageO.setBackgroundResource(userImages[user2ImageIndex])
            binding.userImageO.animateClick()
        }

        binding.buttonMoveNextFragment.setOnClickListener {
            val user1Name = binding.editText1.text.toString()
            val user2Name = binding.editText2.text.toString()

            val bundle = Bundle().apply {
                putInt("user1ImageIndex", user1ImageIndex)
                putInt("user2ImageIndex", user2ImageIndex)
                putString("user1Name", user1Name)
                putString("user2Name", user2Name)
            }

            if (selected == "bolt") {
                findNavController().navigate(R.id.action_mainFragment_to_boltFragment, bundle)
            } else {
                findNavController().navigate(
                    R.id.action_mainFragment_to_ticTacToePairFragment,
                    bundle
                )
            }
            binding.userImageX.animateClick()

            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}