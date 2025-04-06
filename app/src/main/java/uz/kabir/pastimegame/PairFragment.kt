package uz.kabir.pastimegame

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import uz.kabir.pastimegame.AnimationButton.animateClick
import uz.kabir.pastimegame.databinding.FragmentTicTacToePairBinding
import java.util.Random

class PairFragment : Fragment() {
    private var _binding: FragmentTicTacToePairBinding? = null
    private val binding get() = _binding!!

    private lateinit var buttons: Array<Array<ImageButton>>
    private lateinit var statusText: TextView
    private lateinit var resetButton: LinearLayout
    private var currentPlayer = 'X'
    private var countWinnerX = 0
    private var countWinnerO = 0
    private var gameActive = true
    private lateinit var xDrawable: Drawable
    private lateinit var oDrawable: Drawable
    private val winningButtons = mutableListOf<ImageButton>()
    private var gameState = Array(3) { CharArray(3) { ' ' } }
    private val random = Random()

    private var user1ImageIndex: Int = 0
    private var user2ImageIndex: Int = 0
    private var user1Name: String? = null
    private var user2Name: String? = null

    private val userImages = intArrayOf(
        R.drawable.ic_character_woman_1,
        R.drawable.ic_character_woman_5,
        R.drawable.ic_character_woman_4,
        R.drawable.ic_character_man_3,
        R.drawable.ic_character_man_2,
        R.drawable.ic_character_man_4,
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTicTacToePairBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadUserData()
        initializeUI()
        setupButtonClickListeners()
        updateTurnIndicator()
    }

    private fun loadUserData() {
        arguments?.let {
            user1ImageIndex = it.getInt("user1ImageIndex")
            user2ImageIndex = it.getInt("user2ImageIndex")
            user1Name = it.getString("user1Name")
            user2Name = it.getString("user2Name")
        }
        binding.userName2.text = user1Name
        binding.userName.text = user2Name
        binding.userImage2.setBackgroundResource(userImages[user1ImageIndex])
        binding.userImage.setBackgroundResource(userImages[user2ImageIndex])
    }

    private fun initializeUI() {
        xDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_main_x)!!
        oDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_main_o)!!
        statusText = binding.statusText
        resetButton = binding.resetButton

        buttons = Array(3) { row ->
            Array(3) { col ->
                val buttonId = resources.getIdentifier(
                    "button${row}${col}",
                    "id",
                    requireContext().packageName
                )
                requireView().findViewById<ImageButton>(buttonId).apply {
                    backgroundTintList =
                        ContextCompat.getColorStateList(requireContext(), R.color.defaultBackground)
                }
            }
        }
    }

    private fun setupButtonClickListeners() {
        for (i in 0..2) {
            for (j in 0..2) {
                buttons[i][j].setOnClickListener {
                    buttons[i][j].animateClick(40)
                    onGridButtonClick(i, j)
                }
            }
        }
        resetButton.setOnClickListener {
            binding.resetButton.animateClick()
            resetGame()
        }
    }

    private fun updateTurnIndicator() {
        binding.animationView1.visibility =
            if (currentPlayer == 'O') View.VISIBLE else View.INVISIBLE
        binding.animationView2.visibility =
            if (currentPlayer == 'X') View.VISIBLE else View.INVISIBLE
        statusText.text = getString(if (currentPlayer == 'X') R.string.x_turn else R.string.o_turn)
    }

    private fun onGridButtonClick(row: Int, col: Int) {
        if (gameState[row][col] != ' ' || !gameActive) return

        gameState[row][col] = currentPlayer
        buttons[row][col].setImageDrawable(if (currentPlayer == 'X') xDrawable else oDrawable)

        if (checkForWin()) {
            handleWin()
        } else if (isBoardFull()) {
            handleDraw()
        } else {
            switchPlayer()
        }
    }

    private fun handleWin() {
        gameActive = false
        statusText.text =
            getString(if (currentPlayer == 'X') R.string.x_player_won else R.string.o_player_won)
        if (currentPlayer == 'X') {
            countWinnerX++
            binding.indicatorUser2.setImageResource(R.drawable.ic_winner)
        } else {
            countWinnerO++
            binding.indicatorUser1.setImageResource(R.drawable.ic_winner)
        }
        binding.scoreTable.text = "$countWinnerO : $countWinnerX"
        highlightWinningLine()
        disableButtons()
    }

    private fun handleDraw() {
        gameActive = false
        statusText.text = getString(R.string.draw)
    }

    private fun switchPlayer() {
        currentPlayer = if (currentPlayer == 'X') 'O' else 'X'
        updateTurnIndicator()
    }

    private fun checkForWin(): Boolean {
        winningButtons.clear()
        // Check rows
        for (i in 0..2) if (checkLine(
                gameState[i][0],
                gameState[i][1],
                gameState[i][2],
                buttons[i][0],
                buttons[i][1],
                buttons[i][2]
            )
        ) return true
        // Check columns
        for (j in 0..2) if (checkLine(
                gameState[0][j],
                gameState[1][j],
                gameState[2][j],
                buttons[0][j],
                buttons[1][j],
                buttons[2][j]
            )
        ) return true
        // Check diagonals
        if (checkLine(
                gameState[0][0],
                gameState[1][1],
                gameState[2][2],
                buttons[0][0],
                buttons[1][1],
                buttons[2][2]
            )
        ) return true
        if (checkLine(
                gameState[0][2],
                gameState[1][1],
                gameState[2][0],
                buttons[0][2],
                buttons[1][1],
                buttons[2][0]
            )
        ) return true
        return false
    }

    private fun checkLine(
        c1: Char,
        c2: Char,
        c3: Char,
        b1: ImageButton,
        b2: ImageButton,
        b3: ImageButton
    ): Boolean {
        return if (c1 != ' ' && c1 == c2 && c2 == c3) {
            winningButtons.addAll(listOf(b1, b2, b3))
            true
        } else {
            false
        }
    }

    private fun highlightWinningLine() {
        winningButtons.forEach {
            it.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.winBackground)
        }
    }

    private fun isBoardFull(): Boolean {
        return gameState.all { row -> row.all { it != ' ' } }
    }

    private fun disableButtons() {
        buttons.forEach { row -> row.forEach { it.isEnabled = false } }
    }

    private fun resetGame() {
        currentPlayer = if (random.nextBoolean()) 'X' else 'O'
        gameActive = true
        gameState = Array(3) { CharArray(3) { ' ' } }
        for (i in 0..2) {
            for (j in 0..2) {
                buttons[i][j].setImageDrawable(null)
                buttons[i][j].backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.defaultBackground)
                buttons[i][j].isEnabled = true
            }
        }
        winningButtons.clear()
        binding.indicatorUser1.setImageResource(R.drawable.ic_main_o)
        binding.indicatorUser2.setImageResource(R.drawable.ic_main_x)
        updateTurnIndicator()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}