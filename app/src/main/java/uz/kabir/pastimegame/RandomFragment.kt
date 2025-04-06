package uz.kabir.pastimegame

import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import uz.kabir.pastimegame.AnimationButton.animateClick
import uz.kabir.pastimegame.databinding.FragmentTicTacToeRandomBinding
import java.util.Random

class RandomFragment : Fragment() {

    private var _binding: FragmentTicTacToeRandomBinding? = null
    private val binding get() = _binding!!

    private lateinit var buttons: Array<Array<ImageButton>>
    private lateinit var statusText: TextView
    private lateinit var restartButton: LinearLayout
    private var board = Array(3) { CharArray(3) { ' ' } }
    private var currentPlayer = 'X'
    private var aiIsMoving = false

    private var countWinnerX = 0
    private var countWinnerO = 0

    private lateinit var xDrawable: Drawable
    private lateinit var oDrawable: Drawable
    private var winningButtons = mutableListOf<ImageButton>()

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
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTicTacToeRandomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Receive data from Bundle
        arguments?.let {
            user1ImageIndex = it.getInt("user1ImageIndex")
            user1Name = it.getString("user1Name")
        }
        binding.userName2.text = user1Name
        binding.userImage2.setBackgroundResource(userImages[user1ImageIndex])

        statusText = binding.statusText
        restartButton = binding.resetButton
        restartButton.setOnClickListener {
            binding.resetButton.animateClick()
            resetGame()
        }

        buttons = Array(3) { row ->
            Array(3) { col ->
                val buttonID = resources.getIdentifier("button${row}${col}", "id", binding.root.context.packageName)
                binding.root.findViewById<ImageButton>(buttonID).apply {
                    setOnClickListener {
                        it.animateClick(40)
                        makeMove(row, col)
                    }
                }
            }
        }

        xDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_main_x)!!
        oDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_main_o)!!

        // Randomly choose the starting player
        val random = Random()
        currentPlayer = if (random.nextBoolean()) 'X' else 'O'

        // If AI starts, make the first move
        if (currentPlayer == 'O') {
            binding.animationView1.visibility = View.VISIBLE
            binding.animationView2.visibility = View.INVISIBLE
            aiIsMoving = true
            Handler(Looper.getMainLooper()).postDelayed({
                if (_binding != null) {
                    aiRandomMove()
                    currentPlayer = 'X'
                    aiIsMoving = false
                    binding.animationView1.visibility = View.INVISIBLE
                    binding.animationView2.visibility = View.VISIBLE
                }
            }, 1000)
        } else {
            binding.animationView2.visibility = View.VISIBLE
            binding.animationView1.visibility = View.INVISIBLE
        }
    }

    private fun makeMove(row: Int, col: Int) {
        if (aiIsMoving || _binding == null) return
        if (board[row][col] != ' ') {
            return
        }

        board[row][col] = currentPlayer
        buttons[row][col]?.setImageDrawable(if (currentPlayer == 'X') xDrawable else oDrawable)

        if (checkWin(currentPlayer)) {
            if (currentPlayer == 'X') {
                countWinnerX++
                statusText.text = getString(R.string.you_win)
                binding.indicatorUser2.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_winner
                    )
                )
            } else {
                countWinnerO++
                statusText.text = getString(R.string.robot_win)
                binding.indicatorUser1.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_winner
                    )
                )
            }
            highlightWinningLine()
            updateScoreDisplay()
            disableButtons()
            return
        }

        if (isBoardFull()) {
            statusText.text = getString(R.string.draw)
            updateScoreDisplay()
            return
        }

        currentPlayer = if (currentPlayer == 'X') 'O' else 'X'

        if (currentPlayer == 'O') {
            aiIsMoving = true
            binding.animationView1.visibility = View.VISIBLE
            binding.animationView2.visibility = View.INVISIBLE

            Handler(Looper.getMainLooper()).postDelayed({
                if (_binding != null) {
                    aiRandomMove()
                    currentPlayer = 'X'
                    aiIsMoving = false
                    binding.animationView1.visibility = View.INVISIBLE
                    binding.animationView2.visibility = View.VISIBLE
                }
            }, 1000)
        } else {
            binding.animationView2.visibility = View.INVISIBLE
            binding.animationView1.visibility = View.VISIBLE
        }
    }

    private fun aiRandomMove() {
        if (_binding == null) {
            return // Fragment's view has been destroyed, so do nothing
        }

        val emptyCells = mutableListOf<Pair<Int, Int>>()
        for (i in 0..2) {
            for (j in 0..2) {
                if (board[i][j] == ' ') {
                    emptyCells.add(Pair(i, j))
                }
            }
        }

        if (emptyCells.isNotEmpty()) {
            val random = Random()
            val move = emptyCells[random.nextInt(emptyCells.size)]
            board[move.first][move.second] = 'O'
            buttons[move.first][move.second]?.setImageDrawable(oDrawable)

            if (checkWin('O')) {
                statusText.text = getString(R.string.robot_win)
                countWinnerO++
                highlightWinningLine()
                disableButtons()
            } else if (isBoardFull()) {
                statusText.text = getString(R.string.draw)
                updateScoreDisplay()
            }
        }

        // Check binding again before accessing views
        if (_binding != null) {
            if (currentPlayer == 'O') {
                binding.animationView1.visibility = View.VISIBLE
                binding.animationView2.visibility = View.INVISIBLE
            } else {
                binding.animationView2.visibility = View.VISIBLE
                binding.animationView1.visibility = View.INVISIBLE
            }
        }
    }

    private fun checkWin(player: Char): Boolean {
        for (i in 0..2) {
            if (board[i][0] == player && board[i][1] == player && board[i][2] == player) {
                winningButtons.clear()
                winningButtons.add(buttons[i][0]!!)
                winningButtons.add(buttons[i][1]!!)
                winningButtons.add(buttons[i][2]!!)
                return true
            }
            if (board[0][i] == player && board[1][i] == player && board[2][i] == player) {
                winningButtons.clear()
                winningButtons.add(buttons[0][i]!!)
                winningButtons.add(buttons[1][i]!!)
                winningButtons.add(buttons[2][i]!!)
                return true
            }
        }
        if (board[0][0] == player && board[1][1] == player && board[2][2] == player) {
            winningButtons.clear()
            winningButtons.add(buttons[0][0]!!)
            winningButtons.add(buttons[1][1]!!)
            winningButtons.add(buttons[2][2]!!)
            return true
        }
        if (board[0][2] == player && board[1][1] == player && board[2][0] == player) {
            winningButtons.clear()
            winningButtons.add(buttons[0][2]!!)
            winningButtons.add(buttons[1][1]!!)
            winningButtons.add(buttons[2][0]!!)
            return true
        }
        winningButtons.clear()
        return false
    }

    private fun isBoardFull(): Boolean {
        return board.all { row -> row.all { it != ' ' } }
    }

    private fun disableButtons() {
        buttons.forEach { row -> row.forEach { it.isEnabled = false } }
    }

    private fun resetGame() {
        board = Array(3) { CharArray(3) { ' ' } }
        buttons.forEach { row ->
            row.forEach {
                it.setImageDrawable(null)
                it.isEnabled = true
                it.backgroundTintList = null
            }
        }
        val random = Random()
        currentPlayer = if (random.nextBoolean()) 'X' else 'O'
        aiIsMoving = false
        if (currentPlayer == 'O') {
            aiIsMoving = true
            Handler(Looper.getMainLooper()).postDelayed({
                if (_binding != null) {
                    aiRandomMove()
                    currentPlayer = 'X'
                    aiIsMoving = false
                    binding.animationView1.visibility = View.INVISIBLE
                    binding.animationView2.visibility = View.VISIBLE
                }
            }, 1000)
        }
        if (currentPlayer == 'O') {
            binding.animationView1.visibility = View.VISIBLE
            binding.animationView2.visibility = View.INVISIBLE
        } else {
            binding.animationView2.visibility = View.VISIBLE
            binding.animationView1.visibility = View.INVISIBLE
        }
        for (i in 0..2) {
            for (j in 0..2) {
                buttons[i][j]?.setImageDrawable(null)
                buttons[i][j]?.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.defaultBackground)
                buttons[i][j]?.isEnabled = true
            }
        }

        binding.indicatorUser1.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_main_o
            )
        )
        binding.indicatorUser2.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_main_x
            )
        )

        binding.statusText.text = ""

        updateScoreDisplay()
    }

    private fun updateScoreDisplay() {
        binding.scoreTable.text = "$countWinnerO:$countWinnerX"
    }

    private fun highlightWinningLine() {
        for (button in winningButtons) {
            button.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.winBackground)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}