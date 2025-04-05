package uz.kabir.pastimegame

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import uz.kabir.pastimegame.databinding.FragmentTicTacToePairBinding
import java.util.Random


class PairFragment : Fragment() {
    private var _binding: FragmentTicTacToePairBinding? = null
    private val binding get() = _binding!!


    private lateinit var buttons: Array<Array<ImageButton?>>
    private lateinit var statusText: TextView
    private lateinit var resetButton: LinearLayout
    private var currentPlayer = 'X'
    private var countWinnerX = 0
    private var countWinnerO = 0
    private var gameActive = true
    private lateinit var xDrawable: Drawable
    private lateinit var oDrawable: Drawable
    private val winningButtons = mutableListOf<ImageButton>()
    private var gameState = arrayOf(
        arrayOf(' ', ' ', ' '),
        arrayOf(' ', ' ', ' '),
        arrayOf(' ', ' ', ' ')
    )
    private val random = Random() // Add a Random instance

    // Add these variables to receive data
    private var user1ImageIndex: Int = 0
    private var user2ImageIndex: Int = 0
    private var user1Name: String? = null
    private var user2Name: String? = null

    private val userImages = arrayOf(
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
    ): View? {

        _binding = FragmentTicTacToePairBinding.inflate(inflater, container, false)
        val root = binding.root

        // Receive data from Bundle
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


        // Initialize drawables for X and O
        xDrawable = ContextCompat.getDrawable(binding.root.context, R.drawable.ic_main_x)!!
        oDrawable = ContextCompat.getDrawable(binding.root.context, R.drawable.ic_main_o)!!

        statusText = binding.root.findViewById(R.id.statusText)
        resetButton = binding.root.findViewById(R.id.resetButton)

        // Initialize buttons array
        buttons = Array(3) { arrayOfNulls<ImageButton>(3) }

        // Initialize all buttons
        buttons[0][0] = binding.root.findViewById(R.id.button00)
        buttons[0][1] = binding.root.findViewById(R.id.button01)
        buttons[0][2] = binding.root.findViewById(R.id.button02)
        buttons[1][0] = binding.root.findViewById(R.id.button10)
        buttons[1][1] = binding.root.findViewById(R.id.button11)
        buttons[1][2] = binding.root.findViewById(R.id.button12)
        buttons[2][0] = binding.root.findViewById(R.id.button20)
        buttons[2][1] = binding.root.findViewById(R.id.button21)
        buttons[2][2] = binding.root.findViewById(R.id.button22)

        // Set click listeners and initial background
        for (i in 0..2) {
            for (j in 0..2) {
                buttons[i][j]?.setOnClickListener {
                    onGridButtonClick(i, j)
                }
                buttons[i][j]?.backgroundTintList = ContextCompat.getColorStateList(binding.root.context, R.color.defaultBackground)

            }
        }

        resetButton.setOnClickListener {
            resetGame()
        }



        return root

    }

    private fun onGridButtonClick(row: Int, col: Int) {

        if (gameState[row][col] != ' ' || !gameActive) {
            return
        }

        gameState[row][col] = currentPlayer
        buttons[row][col]?.setImageDrawable(if (currentPlayer == 'X') xDrawable else oDrawable)

        if (checkForWin()) {
            gameActive = false
            statusText.text = "Player $currentPlayer Wins!"
            if (currentPlayer == 'X') {
                countWinnerX++
                binding.indicatorUser2.setImageDrawable(
                    ContextCompat.getDrawable(
                        binding.root.context,
                        R.drawable.ic_winner
                    )
                )
            } else {
                countWinnerO++
                binding.indicatorUser1.setImageDrawable(
                    ContextCompat.getDrawable(
                        binding.root.context,
                        R.drawable.ic_winner
                    )
                )
            }
            binding.scoreTable.text = "$countWinnerO : $countWinnerX"

            highlightWinningLine()
        } else if (isBoardFull()) {
            gameActive = false
            statusText.text = "Game Draw!"
        } else {
            currentPlayer = if (currentPlayer == 'X') 'O' else 'X'

            // Update the status text
            if (currentPlayer == 'X') {
                binding.animationView1.visibility = View.INVISIBLE
                binding.animationView2.visibility = View.VISIBLE
            } else {
                binding.animationView1.visibility = View.VISIBLE
                binding.animationView2.visibility = View.INVISIBLE

            }

            statusText.text = "Player $currentPlayer's Turn"
        }
    }

    private fun checkForWin(): Boolean {
        winningButtons.clear()

        // Check rows
        for (i in 0..2) {
            if (gameState[i][0] != ' ' && gameState[i][0] == gameState[i][1] && gameState[i][0] == gameState[i][2]) {
                winningButtons.addAll(listOf(buttons[i][0]!!, buttons[i][1]!!, buttons[i][2]!!))
                return true
            }
        }

        // Check columns
        for (j in 0..2) {
            if (gameState[0][j] != ' ' && gameState[0][j] == gameState[1][j] && gameState[0][j] == gameState[2][j]) {
                winningButtons.addAll(listOf(buttons[0][j]!!, buttons[1][j]!!, buttons[2][j]!!))
                return true
            }
        }

        // Check diagonals
        if (gameState[0][0] != ' ' && gameState[0][0] == gameState[1][1] && gameState[0][0] == gameState[2][2]) {
            winningButtons.addAll(listOf(buttons[0][0]!!, buttons[1][1]!!, buttons[2][2]!!))
            return true
        }
        if (gameState[0][2] != ' ' && gameState[0][2] == gameState[1][1] && gameState[0][2] == gameState[2][0]) {
            winningButtons.addAll(listOf(buttons[0][2]!!, buttons[1][1]!!, buttons[2][0]!!))
            return true
        }

        return false
    }

    private fun highlightWinningLine() {
        for (button in winningButtons) {
            button.backgroundTintList = ContextCompat.getColorStateList(binding.root.context, R.color.winBackground)
        }
        // Disable all buttons after win
        for (i in 0..2) {
            for (j in 0..2) {
                buttons[i][j]?.isEnabled = false
            }
        }
    }

    private fun isBoardFull(): Boolean {
        for (i in 0..2) {
            for (j in 0..2) {
                if (gameState[i][j] == ' ') {
                    return false
                }
            }
        }
        return true
    }

    private fun resetGame() {
        currentPlayer = if (random.nextBoolean()) 'X' else 'O'
        gameActive = true
        gameState = arrayOf(
            arrayOf(' ', ' ', ' '),
            arrayOf(' ', ' ', ' '),
            arrayOf(' ', ' ', ' ')
        )
        for (i in 0..2) {
            for (j in 0..2) {
                buttons[i][j]?.setImageDrawable(null)
                buttons[i][j]?.backgroundTintList = ContextCompat.getColorStateList(binding.root.context, R.color.defaultBackground)
                buttons[i][j]?.isEnabled = true
            }
        }

        winningButtons.clear()
        statusText.text = "Player X's Turn"
        binding.indicatorUser1.setImageDrawable(
            ContextCompat.getDrawable(
                binding.root.context,
                R.drawable.ic_main_o
            )
        )
        binding.indicatorUser2.setImageDrawable(
            ContextCompat.getDrawable(
                binding.root.context,
                R.drawable.ic_main_x
            )
        )

        if (currentPlayer == 'X') {
            binding.animationView1.visibility = View.INVISIBLE
            binding.animationView2.visibility = View.VISIBLE
        } else {
            binding.animationView1.visibility = View.VISIBLE
            binding.animationView2.visibility = View.INVISIBLE
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}