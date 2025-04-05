package uz.kabir.pastimegame

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import uz.kabir.pastimegame.databinding.FragmentBottomSheetOnePlayerBinding

class BottomSheetOnePlayer : BottomSheetDialogFragment() {

    private var _binding: FragmentBottomSheetOnePlayerBinding? = null
    private val binding get() = _binding!!

    private var user1ImageIndex = 0
    private var isHardMode = false

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
        _binding = FragmentBottomSheetOnePlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateUserImages()
        setupClickListeners()
        updateModeButtons()
    }

    private fun updateUserImages() {
        binding.userImageX.setBackgroundResource(userImages[user1ImageIndex])
    }

    private fun setupClickListeners() {
        binding.userImageX.setOnClickListener {
            user1ImageIndex = (user1ImageIndex + 1) % userImages.size
            updateUserImages()
        }


        binding.buttonHard.setOnClickListener {
            isHardMode = true
            updateModeButtons()
        }

        binding.buttonEasy.setOnClickListener {
            isHardMode = false
            updateModeButtons()
        }

        binding.buttonMoveNextFragment.setOnClickListener {
            val user1Name = binding.editText1.text.toString()
            val bundle = Bundle().apply {
                putInt("user1ImageIndex", user1ImageIndex)
                putString("user1Name", user1Name)
            }
            if (isHardMode) {
                findNavController().navigate(R.id.action_mainFragment_to_ticTacToeAIFragment,bundle)
            } else {
                findNavController().navigate(R.id.action_mainFragment_to_ticTacToeRandomFragment,bundle)
            }
            dismiss()
        }
    }

    private fun updateModeButtons() {
        val hardColor = if (isHardMode) R.color.winBackground else R.color.white
        val easyColor = if (isHardMode) R.color.white else R.color.winBackground
        binding.buttonHard.backgroundTintList =
            ContextCompat.getColorStateList(requireContext(), hardColor)
        binding.buttonEasy.backgroundTintList =
            ContextCompat.getColorStateList(requireContext(), easyColor)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}