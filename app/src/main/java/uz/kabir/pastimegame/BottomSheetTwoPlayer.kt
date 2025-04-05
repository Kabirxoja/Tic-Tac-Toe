package uz.kabir.pastimegame

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.userImageX.setBackgroundResource(userImages[user1ImageIndex])
        binding.userImageO.setBackgroundResource(userImages[user2ImageIndex])

        binding.userImageX.setOnClickListener {
            user1ImageIndex = (user1ImageIndex + 1) % userImages.size
            binding.userImageX.setBackgroundResource(userImages[user1ImageIndex])
        }

        binding.userImageO.setOnClickListener {
            user2ImageIndex = (user2ImageIndex + 1) % userImages.size
            binding.userImageO.setBackgroundResource(userImages[user2ImageIndex])
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


            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}