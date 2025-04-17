package uz.kabir.pastimegame

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.audiofx.Equalizer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import uz.kabir.pastimegame.AnimationButton.animateClick
import uz.kabir.pastimegame.MySharedPreference.getStateAudio
import uz.kabir.pastimegame.databinding.FragmentTicTacToeAIBinding


class AIFragment : Fragment() {

    private var _binding: FragmentTicTacToeAIBinding? = null
    private val binding get() = _binding!!

    private lateinit var buttons: Array<Array<ImageButton>>
    private lateinit var statusText: TextView
    private lateinit var restartButton: LinearLayout
    private var board = Array(3) { CharArray(3) { ' ' } }

    private lateinit var xDrawable: Drawable
    private lateinit var oDrawable: Drawable

    private var user1ImageIndex: Int = 0
    private var user1Name: String? = null

    private val userImages = arrayOf(
        R.drawable.ic_character_woman_1,
        R.drawable.ic_character_woman_5,
        R.drawable.ic_character_woman_4,
        R.drawable.ic_character_man_3,
        R.drawable.ic_character_man_2,
        R.drawable.ic_character_man_4,
    )

    private var countWinnerX = 0
    private var countWinnerO = 0
    private var isXTurn = true

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var equalizer: Equalizer
    private var audioOn = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTicTacToeAIBinding.inflate(inflater, container, false)
        val root = binding.root

        arguments?.let {
            user1ImageIndex = it.getInt("user1ImageIndex")
            user1Name = it.getString("user1Name")
        }

        audioOn = getStateAudio(requireContext())

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.userName2.text = user1Name
        binding.userImage2.setBackgroundResource(userImages[user1ImageIndex])

        statusText = binding.root.findViewById(R.id.statusText)

        restartButton = binding.root.findViewById(R.id.resetButton)
        restartButton.setOnClickListener {
            binding.resetButton.animateClick()
            resetGame()
        }

        buttons = Array(3) { row ->
            Array(3) { col ->
                val buttonID = resources.getIdentifier(
                    "button${row}${col}",
                    "id",
                    binding.root.context.packageName
                )
                binding.root.findViewById<ImageButton>(buttonID).apply {
                    setOnClickListener {
                        it.animateClick(40)
                        makeMove(row, col)
                    }
                }
            }
        }

        xDrawable = ContextCompat.getDrawable(binding.root.context, R.drawable.ic_main_x)!!
        oDrawable = ContextCompat.getDrawable(binding.root.context, R.drawable.ic_main_o)!!


    }

    private fun makeMove(row: Int, col: Int) {
        if (board[row][col] != ' ' || !isXTurn) return

        board[row][col] = 'X'
        buttons[row][col].setImageDrawable(xDrawable)

        isXTurn = false
        binding.animationView1.visibility = View.VISIBLE
        binding.animationView2.visibility = View.INVISIBLE
        if (audioOn) setAudioFile(true)

        val winPositions = getWinningPositions('X')
        if (winPositions != null) {
            statusText.text = resources.getString(R.string.you_win)
            countWinnerX++
            highlightWinningButtons(winPositions)
            binding.indicatorUser2.setImageDrawable(ContextCompat.getDrawable(binding.root.context, R.drawable.ic_winner))
            disableButtons()
            return
        }

        if (isBoardFull()) {
            statusText.text = resources.getString(R.string.draw)
            return
        }

        updateScoreDisplay()

        // Delay AI move to simulate thinking
        Handler(Looper.getMainLooper()).postDelayed({
            aiMove()
        }, 1000)
    }


    private fun aiMove() {
        val bestMove = minimaxMove()
        board[bestMove.first][bestMove.second] = 'O'
        buttons[bestMove.first][bestMove.second].setImageDrawable(oDrawable)

        val winPositions = getWinningPositions('O')
        if (winPositions != null) {
            statusText.text = resources.getString(R.string.robot_win)
            countWinnerO++
            highlightWinningButtons(winPositions)
            binding.indicatorUser1.setImageDrawable(
                ContextCompat.getDrawable(
                    binding.root.context,
                    R.drawable.ic_winner
                )
            )
            disableButtons()
            return
        }

        if (isBoardFull()) {
            statusText.text = resources.getString(R.string.draw)
            return
        }

        isXTurn = true
        binding.animationView1.visibility = View.INVISIBLE
        binding.animationView2.visibility = View.VISIBLE
        if(audioOn) setAudioFile(false)
    }

    private fun minimaxMove(): Pair<Int, Int> {
        var bestScore = Int.MIN_VALUE
        var move = Pair(-1, -1)

        for (i in 0..2) {
            for (j in 0..2) {
                if (board[i][j] == ' ') {
                    board[i][j] = 'O'
                    val score = minimax(false)
                    board[i][j] = ' '
                    if (score > bestScore) {
                        bestScore = score
                        move = Pair(i, j)
                    }
                }
            }
        }
        return move
    }

    private fun minimax(isMaximizing: Boolean): Int {
        when {
            getWinningPositions('O') != null -> return 1
            getWinningPositions('X') != null -> return -1
            isBoardFull() -> return 0
        }

        var bestScore = if (isMaximizing) Int.MIN_VALUE else Int.MAX_VALUE

        for (i in 0..2) {
            for (j in 0..2) {
                if (board[i][j] == ' ') {
                    board[i][j] = if (isMaximizing) 'O' else 'X'
                    val score = minimax(!isMaximizing)
                    board[i][j] = ' '
                    bestScore = if (isMaximizing) maxOf(score, bestScore) else minOf(score, bestScore)
                }
            }
        }
        return bestScore
    }

    private fun getWinningPositions(player: Char): List<Pair<Int, Int>>? {
        for (i in 0..2) {
            if (board[i][0] == player && board[i][1] == player && board[i][2] == player) {
                return listOf(Pair(i, 0), Pair(i, 1), Pair(i, 2))
            }
        }
        for (i in 0..2) {
            if (board[0][i] == player && board[1][i] == player && board[2][i] == player) {
                return listOf(Pair(0, i), Pair(1, i), Pair(2, i))
            }
        }
        if (board[0][0] == player && board[1][1] == player && board[2][2] == player) {
            return listOf(Pair(0, 0), Pair(1, 1), Pair(2, 2))
        }
        if (board[0][2] == player && board[1][1] == player && board[2][0] == player) {
            return listOf(Pair(0, 2), Pair(1, 1), Pair(2, 0))
        }
        return null
    }

    private fun highlightWinningButtons(positions: List<Pair<Int, Int>>) {
        positions.forEach { (row, col) ->
            buttons[row][col].backgroundTintList = ContextCompat.getColorStateList(binding.root.context, R.color.winBackground)
        }
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
            }
        }

        statusText.text = ""
        isXTurn = true

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

        for (i in 0..2) {
            for (j in 0..2) {
                buttons[i][j]?.backgroundTintList =
                    ContextCompat.getColorStateList(binding.root.context, R.color.defaultBackground)
                buttons[i][j]?.isEnabled = true
            }
        }

        updateScoreDisplay()
    }

    private fun updateScoreDisplay() {
        binding.scoreTable.text = "$countWinnerO:$countWinnerX"
    }

    private fun setAudioFile(boolean: Boolean) {
        if (boolean) {
            mediaPlayer = MediaPlayer.create(binding.root.context, R.raw.audio_1)
            mediaPlayer.setVolume(0.3f, 0.3f)
            Log.d("birinchi", "Correct answer")
        } else {
            mediaPlayer = MediaPlayer.create(binding.root.context, R.raw.audio_2)
            mediaPlayer.setVolume(0.3f, 0.3f)
            Log.d("birinchi", "Mistake answer")
        }

        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
        mediaPlayer.start()


        mediaPlayer.setOnPreparedListener {
            // Check if the Equalizer is supported on this device
            try {
                equalizer = Equalizer(0, mediaPlayer.audioSessionId).apply {
                    enabled = true
                    // Configure each band of the Equalizer
                    for (i in 0 until numberOfBands) {
                        setBandLevel(
                            i.toShort(),
                            1000.toShort()
                        ) // Example: increase each band's level
                    }
                }
                Log.d("Equalizer", "Equalizer successfully configured")
            } catch (e: Exception) {
                Log.e("Equalizer", "Failed to initialize Equalizer: ${e.message}")
            }
        }

        // Set an OnCompletionListener to release the MediaPlayer when done
        mediaPlayer.setOnCompletionListener {
            it.release()
            Log.d("MediaPlayer", "MediaPlayer released")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
